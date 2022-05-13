package chat.sphinx.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState

@Composable
expect fun MessageMenu(
    chatMessage: ChatMessage,
    editMessageState: EditMessageState,
    isVisible: MutableState<Boolean>
)