package chat.sphinx.common.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.utils.toAnnotatedString
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    replyToTextAction: (chatMessage: ChatMessage) -> Unit,
    isVisible: MutableState<Boolean>
) {
    val dismissKebab = {
        isVisible.value = false
    }
    CursorDropdownMenu(
        expanded = isVisible.value,
        onDismissRequest = dismissKebab
    ) {
        chatMessage.message.retrieveTextToShow()?.let { messageText ->
            if (messageText.isNotEmpty()) {
                val clipboardManager = LocalClipboardManager.current
                DropdownMenuItem(onClick = {
                    clipboardManager.setText(
                        messageText.toAnnotatedString()
                    )
                    dismissKebab()
                }) {
                    Text("copy text")
                }
            }
        }

        DropdownMenuItem(onClick = {
            replyToTextAction(chatMessage)
            dismissKebab()
        }) {
            Text("reply")
        }
        if (chatMessage.isReceived) {
            DropdownMenuItem(onClick = {
                // TODO: Boost chatMessage
                dismissKebab()
            }) {
                Text("boost")
            }
        }
        if (chatMessage.isSent) {
            DropdownMenuItem(onClick = {
                // TODO: Delete chatMessage
                dismissKebab()
            }) {
                Text("delete")
            }
        }
    }
}