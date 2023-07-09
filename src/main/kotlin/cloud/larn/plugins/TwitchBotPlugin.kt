package cloud.larn.plugins

import cloud.larn.plugins.config.TwitchBotPluginConfiguration
import cloud.larn.websocket.inputMessages
import cloud.larn.websocket.outputMessages
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

val TwitchBotPlugin = createApplicationPlugin(
    name = "TwitchBotPlugin",
    configurationPath = "bot",
    createConfiguration = ::TwitchBotPluginConfiguration
) {
    val accessToken = pluginConfig.accessToken
    val runBot = pluginConfig.runContinuousBot
    val twitchUser = pluginConfig.twitchUser
    val twitchChannel = pluginConfig.twitchChannel

    if (accessToken.isNullOrBlank() || twitchUser.isNullOrBlank() || twitchChannel.isNullOrBlank()) {
        application.log.error("Configuration is missing, shutting down")
        Runtime.getRuntime().exit(-1)
    } else {
        this.setupBot(twitchUser, twitchChannel, accessToken, runBot)
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun PluginBuilder<TwitchBotPluginConfiguration>.setupBot(
    twitchUser: String,
    twitchChannel: String,
    accessToken: String,
    runBot: Boolean = true
) {
    val botThreadContext = newSingleThreadContext("BotThread")
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    this.application.routing {
        get("/start") {
            println("Starting bot...")
            call.respond(HttpStatusCode.OK)

            if (runBot) {
                runBlocking {
                    withContext(botThreadContext) {
                        client.webSocket(
                            method = HttpMethod.Get,
                            host = "irc-ws.chat.twitch.tv",
                            port = 80,
                            path = "/"
                        ) {
                            val messageOutputRoutine = launch { outputMessages(twitchChannel) }
                            val userInputRoutine = launch { inputMessages() }

                            outgoing.send(Frame.Text("CAP REQ :twitch.tv/membership twitch.tv/tags twitch.tv/commands"))
                            outgoing.send(Frame.Text("PASS oauth:$accessToken"))
                            outgoing.send(Frame.Text("NICK $twitchUser"))
                            outgoing.send(Frame.Text("JOIN #$twitchChannel"))
                            outgoing.send(Frame.Text("PRIVMSG #$twitchChannel :${twitchUser} bot is running peepoHappy"))

                            userInputRoutine.join() // Wait for completion; either "exit" or error
                            messageOutputRoutine.cancelAndJoin()
                        }
                    }
                }
            }
        }
    }

    on(MonitoringEvent(ApplicationStarted)) { application ->
        application.log.info("TwitchBotPlugin is started")
    }
}