package chat.sphinx.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
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
        if (chatMessage.isReceived) {
            DropdownMenuItem(onClick = {
                // TODO: Boost is broken...
                chatMessage.boostMessage()
                dismissKebab()
            }) {
                OptionItem("Boost",Res.drawable.ic_boost_green)
            }
        }
        chatMessage.message.retrieveTextToShow()?.let { messageText ->
            if (messageText.isNotEmpty()) {
                val clipboardManager = LocalClipboardManager.current
                DropdownMenuItem(onClick = {
                    clipboardManager.setText(
                        messageText.toAnnotatedString()
                    )
                    dismissKebab()
                }) {
                    OptionItem("Copy text", imageVector = Icons.Default.ContentCopy)
                }
            }
        }
        DropdownMenuItem(onClick = {
            chatMessage.setAsReplyToMessage(editMessageState)
            dismissKebab()
        }) {
           OptionItem("Reply", imageVector = Icons.Default.Reply)
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
@Composable
fun OptionItem(optionText:String,iconPath:String?=null,imageVector: ImageVector?=null){
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        iconPath?.let {
            Image(
                painter = imageResource(it),
                contentDescription = "Sphinx Background",
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.FillBounds
            )
        }
        imageVector?.let {
            Icon(it,contentDescription = null,modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(optionText, color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
    }
}