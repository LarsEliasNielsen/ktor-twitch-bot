package cloud.larn.irc.model

data class IrcMessage(
    val tags: Map<String, String>?,
    val source: Source?,
    val command: Command,
    val params: String?
) {
    data class Command(
        val command: String,
        val channel: String? = null,
        val additional: String? = null
    )

    data class Source(
        val nick: String?,
        val host: String
    )
}