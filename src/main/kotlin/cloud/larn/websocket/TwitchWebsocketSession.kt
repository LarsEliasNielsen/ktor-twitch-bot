package cloud.larn.websocket

import cloud.larn.irc.IrcMessageParser
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import java.lang.Exception

suspend fun DefaultClientWebSocketSession.outputMessages(twitchChannel: String) {
    try {
        for (frame in incoming) {
            frame as? Frame.Text ?: continue

            val rawMessage: String = frame.readText()

            rawMessage.lines()
                .filter { it.isNotBlank() }
                .forEach { line ->
                    IrcMessageParser.parse(line)?.let { message ->
                        println(message)

                        if (message.command.command == "PRIVMSG" && message.params == "!pingbot") {
                            val user = message.source?.nick
                            if (user.isNullOrBlank()) {
                                outgoing.send(Frame.Text("PRIVMSG #$twitchChannel :Pong!"))
                            } else {
                                outgoing.send(Frame.Text("PRIVMSG #$twitchChannel :@$user Pong!"))
                            }
                        }
                    }
                }
        }
    } catch (e: Exception) {
        println("Error while receiving: ${e.localizedMessage}")
    }
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
    while (true) {
        val message = readlnOrNull() ?: ""
        if (message.equals("exit", ignoreCase = true)) return
        try {
            send(message)
        } catch (e: Exception) {
            println("Error while sending: ${e.localizedMessage}")
            return
        }
    }
}