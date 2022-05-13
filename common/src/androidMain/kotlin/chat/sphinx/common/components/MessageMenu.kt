package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    editMessageState: EditMessageState,
    isVisible: MutableState<Boolean>
) {
    Text(
        text = "Not supported"
    )
}