package chat.sphinx.common.components.landing

import androidx.compose.runtime.Composable

@Composable
internal expect fun SphinxDialog(
    title: String,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit
)