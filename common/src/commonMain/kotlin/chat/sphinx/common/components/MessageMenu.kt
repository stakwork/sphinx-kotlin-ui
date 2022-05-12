package chat.sphinx.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.SphinxState

@Composable
expect fun MessageMenu(
    chatMessage: ChatMessage,
    replyToTextAction: () -> Unit,
    isVisible: MutableState<Boolean>
)