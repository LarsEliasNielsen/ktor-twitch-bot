package cloud.larn

import cloud.larn.plugins.TwitchBotPlugin
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testBotStart() = testApplication {
        environment {
            config = MapApplicationConfig(
                "bot.user" to "bot",
                "bot.channel" to "twitch"
            )
        }
        application {
            install(TwitchBotPlugin) {
                this.accessToken = "abc123"
                this.runContinuousBot = false
            }
        }

        val response = client.get("/start")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
