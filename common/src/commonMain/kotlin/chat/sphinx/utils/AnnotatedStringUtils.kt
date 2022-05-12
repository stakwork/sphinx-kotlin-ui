package chat.sphinx.utils

import androidx.compose.ui.text.AnnotatedString

inline fun String.toAnnotatedString(): AnnotatedString {
    return AnnotatedString(this)
}