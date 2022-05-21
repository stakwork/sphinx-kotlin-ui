package chat.sphinx.utils.linkify

class LinkSpec(
    val tag: String,
    val url: String,
    val start: Int = 0,
    val end: Int = 0,
)