package chat.sphinx.common.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.toAnnotatedString
import chat.sphinx.wrapper.message.isMediaAttachmentAvailable
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
actual fun MessageMenu(
    chatMessage: ChatMessage,
    editMessageState: EditMessageState,
    isVisible: MutableState<Boolean>,
    chatViewModel: ChatViewModel
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
            chatMessage.setAsReplyToMessage(editMessageState)
            dismissKebab()
        }) {
            Text("reply")
        }
        if (chatMessage.message.isMediaAttachmentAvailable) {
            DropdownMenuItem(onClick = {
                // TODO: Save attachment...
                chatViewModel.editMessageState
                dismissKebab()
            }) {
                Text("save attachment")
            }
        }
        if (chatMessage.isReceived) {
            DropdownMenuItem(onClick = {
                // TODO: Boost is broken...
                chatMessage.boostMessage()
                dismissKebab()
            }) {
                Text("boost")
            }
        }
        if (chatMessage.isSent) {
            DropdownMenuItem(onClick = {
                // TODO: Confirm action...
                chatMessage.deleteMessage()
                dismissKebab()
            }) {
                Text(
                    "delete",
                    color = Color.Red
                )
            }
        }
    }
}