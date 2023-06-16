package cloud.larn.plugins

import cloud.larn.plugins.config.TwitchBotPluginConfiguration
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

val TwitchBotPlugin = createApplicationPlugin(
    name = "TwitchBotPlugin",
    configurationPath = "bot",
    createConfiguration = ::TwitchBotPluginConfiguration
) {
    val accessToken = pluginConfig.accessToken
    val twitchUser = pluginConfig.twitchUser
    val twitchChannel = pluginConfig.twitchChannel

    if (accessToken.isNullOrBlank() || twitchUser.isNullOrBlank() || twitchChannel.isNullOrBlank()) {
        application.log.error("Configuration is missing, shutting down")
        Runtime.getRuntime().exit(-1)
    } else {
        this.setupBot()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun PluginBuilder<TwitchBotPluginConfiguration>.setupBot() {
    val botThreadContext = newSingleThreadContext("BotThread")
    val client = HttpClient(CIO) {
        install(WebSockets)
    }

    this.application.routing {
        get("start") {
            println("Starting bot...")
        }
    }

    on(MonitoringEvent(ApplicationStarted)) { application ->
        application.log.info("TwitchBotPlugin is started")
    }
}