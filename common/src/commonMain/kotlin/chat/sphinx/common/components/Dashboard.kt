package chat.sphinx.common.components

import androidx.compose.runtime.Composable
import chat.sphinx.common.state.SphinxState


@Composable
expect fun Dashboard(
    sphinxState: SphinxState
)