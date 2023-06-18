package cloud.larn.irc

import cloud.larn.irc.model.IrcMessage

// https://dev.twitch.tv/docs/irc/example-parser/

object IrcMessageParser {
    fun parse(message: String): IrcMessage? {
        var idx = 0

        var rawTagsComponent: String? = null
        var rawSourceComponent: String? = null
        var rawCommandComponent: String? = null
        var rawParametersComponent: String? = null

        // Tags
        if (message.startsWith("@")) {
            val endIdx = message.indexOf(" ")
            rawTagsComponent = message.substring(1 until endIdx)
            idx = endIdx + 1
        }

        // Source
        if (message[idx] == ':') {
            idx += 1
            val endIdx = message.indexOf(" ", idx)
            rawSourceComponent = message.substring(idx until endIdx)
            idx = endIdx + 1
        }

        // Command
        var endIdx = message.indexOf(":", idx)
        if (endIdx == -1) {
            endIdx = message.length
        }
        rawCommandComponent = message.substring(idx until endIdx).trim()

        // Parameters
        if (endIdx != message.length) {
            idx = endIdx + 1
            rawParametersComponent = message.substring(idx)
        }

//        println("rawTagsComponent: $rawTagsComponent")
//        println("rawSourceComponent: $rawSourceComponent")
//        println("rawCommandComponent: $rawCommandComponent")
//        println("rawParametersComponent: $rawParametersComponent")
//        println("---")

        val command = parseCommand(rawCommandComponent)
        return if (command != null) {
            IrcMessage(
                tags = rawTagsComponent?.let { parseTags(it) },
                source = rawSourceComponent?.let { parseSource(it) },
                command = command,
                params = rawParametersComponent
            )
        } else {
            null
        }
    }

    private fun parseTags(tags: String): Map<String, String> {
        return tags.split(";").associate {
            val (key, value) = it.split("=")
            key to value
        }
    }

    private fun parseCommand(command: String): IrcMessage.Command? {
        val commandParts = command.split(" ")

        return when (commandParts.first().uppercase()) {
            "PING", "GLOBALUSERSTATE" -> {
                IrcMessage.Command(
                    command = commandParts.first()
                )
            }
            "CAP" -> {
                IrcMessage.Command(
                    command = commandParts.first(),
                    additional = "Capabilities acknowledged: ${commandParts.contains("ACK")}"
                )
            }
            "RECONNECT" -> {
                IrcMessage.Command(
                    command = commandParts.first(),
                    additional = "Server restarting"
                )
            }
            "421" -> {
                IrcMessage.Command(
                    command = commandParts.first(),
                    additional = "Unsupported IRC command: ${commandParts.first()}"
                )
            }
            "001" -> {
                IrcMessage.Command(
                    command = commandParts.first(),
                    additional = "Authentication successful"
                )
            }
            "002", "003", "004", "353", "366", "372", "375", "376" -> {
                null
            }
            else -> {
                IrcMessage.Command(
                    command = commandParts.first(),
                    channel = commandParts.getOrNull(1)
                )
            }
        }
    }

    private fun parseSource(source: String): IrcMessage.Source {
        val sourceParts = source.split("!")
        return IrcMessage.Source(
            nick = if (sourceParts.size > 1) sourceParts[0] else null,
            host = if (sourceParts.size > 1) sourceParts[1] else sourceParts[0],
        )
    }
}