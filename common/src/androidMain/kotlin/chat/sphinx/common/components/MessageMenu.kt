package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    isVisible: MutableState<Boolean>,
    chatViewModel: ChatViewModel
) {
    Text(
        text = "Not supported"
    )
}