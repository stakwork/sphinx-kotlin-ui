package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import chat.sphinx.common.state.SphinxState

@Composable
actual fun Dashboard(
    sphinxState: SphinxState
) {
    Text(
        text = "Hello Word"
    )
}