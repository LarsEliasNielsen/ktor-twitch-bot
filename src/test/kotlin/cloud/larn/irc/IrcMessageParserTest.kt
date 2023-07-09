package cloud.larn.irc

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class IrcMessageParserTest {

    @Test
    fun parse_parseTags_returnsMapOfNullableTags() {
        val tagsSet = mutableMapOf(
            "display-name" to "TwitchUser",
            "hello" to "world"
        )
        val rawIrcMessage = buildRawIrcMessage(tags = tagsSet.entries.joinToString(separator = ";"))
        val message = IrcMessageParser.parse(rawIrcMessage)

        assertNotNull(message?.tags)
        assertContentEquals(tagsSet.toList(), message?.tags?.toList())
    }

    @Test
    fun parse_parseSource_returnsNickAndHost() {
        val nick = "nick"
        val host = "host"
        val rawIrcMessage = buildRawIrcMessage(sourceNick = nick, sourceHost = host)
        println(rawIrcMessage)
        val message = IrcMessageParser.parse(rawIrcMessage)
        println(message)

        assertNotNull(message?.source)
        assertEquals(nick, message?.source?.nick)
        assertEquals(host, message?.source?.host)
    }

    @Test
    fun parse_parseCommand_returnsCommandAndChannel() {
        val command = "command"
        val channel = "#channel"
        val rawIrcMessage = buildRawIrcMessage(command = command, channel = channel)
        val message = IrcMessageParser.parse(rawIrcMessage)

        assertNotNull(message?.command)
        assertEquals(command, message?.command?.command)
        assertEquals(channel, message?.command?.channel)
    }

    @Test
    fun parse_parseParams_returnsMessage() {
        val chatMessage = "message"
        val rawIrcMessage = buildRawIrcMessage(params = chatMessage)
        val message = IrcMessageParser.parse(rawIrcMessage)

        assertNotNull(message?.command)
        assertEquals(chatMessage, message?.params)
    }

    private fun buildRawIrcMessage(
        tags: String = mutableMapOf("display-name" to "TwitchUser").entries.joinToString(separator = ";"),
        sourceNick: String = "twitchuser",
        sourceHost: String = "twitchuser@twitch.tv",
        command: String = "PRIVMSG",
        channel: String = "#twitchchannel",
        params: String = "Hello, World!"
    ): String = "@${tags} :${sourceNick}!${sourceHost} ${command} ${channel} :${params}"
}