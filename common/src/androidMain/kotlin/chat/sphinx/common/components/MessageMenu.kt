package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chat.sphinx.common.models.ChatMessage

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    replyToTextAction: (chatMessage: ChatMessage) -> Unit,
    isVisible: MutableState<Boolean>
) {
    Text(
        text = "Not supported"
    )
}