package chat.sphinx.utils

import androidx.compose.ui.text.AnnotatedString

/**
 * Turn string to an annotated string (with clickable/highlighted text).
 */
fun String.toAnnotatedString(): AnnotatedString {
    return AnnotatedString(this)
}