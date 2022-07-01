package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        onDismissRequest = dismissKebab, modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer).clip(
            RoundedCornerShape(16.dp)
        )
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
                    Text("Copy text", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
                }
            }
        }

        DropdownMenuItem(onClick = {
            chatMessage.setAsReplyToMessage(editMessageState)
            dismissKebab()
        }) {
            Text("reply", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
        }
        if (chatMessage.message.isMediaAttachmentAvailable) {
            DropdownMenuItem(onClick = {
                // TODO: Save attachment...
                chatViewModel.editMessageState
                dismissKebab()
            }) {
                Text("Save attachment", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
            }
        }
        if (chatMessage.isReceived) {
            DropdownMenuItem(onClick = {
                // TODO: Boost is broken...
                chatMessage.boostMessage()
                dismissKebab()
            }) {
                Text("Boost", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
            }
        }
        if (chatMessage.isSent) {
            DropdownMenuItem(onClick = {
                // TODO: Confirm action...
                chatMessage.deleteMessage()
                dismissKebab()
            }) {
                Text(
                    "Delete",
                    color = Color.Red
                )
            }
        }
    }
}