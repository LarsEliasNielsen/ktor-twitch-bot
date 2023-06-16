package cloud.larn

import io.ktor.server.application.*
import cloud.larn.plugins.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val accessToken: String = System.getenv("access_token") ?: ""
    if (developmentMode) {
        if (accessToken.isBlank()) {
            println("Missing Twitch access token!")
        }
    }

    install(TwitchBotPlugin) {
        this.accessToken = accessToken
    }
}
