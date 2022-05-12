package chat.sphinx.common.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import chat.sphinx.common.models.ChatMessage

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    replyToTextAction: () -> Unit,
    isVisible: MutableState<Boolean>
) {
    CursorDropdownMenu(
        expanded = isVisible.value,
        onDismissRequest = {
            isVisible.value = false
        }
    ) {
        DropdownMenuItem(onClick = { /* Handle refresh! */ }) {
            Text("copy text")
        }
        DropdownMenuItem(onClick = replyToTextAction) {
            Text("reply")
        }
        if (chatMessage.isReceived) {
            DropdownMenuItem(onClick = { /* Handle send feedback! */ }) {
                Text("boost")
            }
        }
        if (chatMessage.isSent) {
            DropdownMenuItem(onClick = { /* Handle send feedback! */ }) {
                Text("delete")
            }
        }
    }
}