package cloud.larn.plugins.config

import io.ktor.server.config.*

class TwitchBotPluginConfiguration(config: ApplicationConfig) {
    var accessToken: String? = null
    var runContinuousBot: Boolean = true
    var twitchUser: String? = config.tryGetString("user")
    var twitchChannel: String? = config.tryGetString("channel")
}