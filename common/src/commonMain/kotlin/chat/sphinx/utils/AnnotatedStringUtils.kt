package chat.sphinx.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import chat.sphinx.utils.linkify.SphinxLinkify
import chat.sphinx.utils.linkify.urlSpanStyle

/**
 * Turn string to an annotated string (with clickable/highlighted text).
 */
fun String.toAnnotatedString(): AnnotatedString {
    val links = SphinxLinkify.gatherLinks(
        text = this,
        mask = SphinxLinkify.ALL
    )
    return AnnotatedString.Builder(this).also { builder ->
        links.forEach { linkSpec ->
            builder.addStyle(
                style = urlSpanStyle,
                start = linkSpec.start,
                end = linkSpec.end
            )
            builder.addStringAnnotation(
                tag = linkSpec.tag,
                annotation = linkSpec.url,
                start = linkSpec.start,
                end = linkSpec.end
            )
            // TODO: selectable text...

        }
    }.toAnnotatedString()
}

fun String.containLinksWithPreview(): Boolean {
    val links = SphinxLinkify.gatherLinks(
        text = this,
        mask = SphinxLinkify.LINKS_WITH_PREVIEWS
    )
    return links.isNotEmpty()
}