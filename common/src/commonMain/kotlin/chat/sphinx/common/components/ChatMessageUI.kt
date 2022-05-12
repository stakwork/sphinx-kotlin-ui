package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.components.chat.KebabMenu
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.chat.ChatType
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.chatTimeFormat
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.isImage

@Composable
fun ChatMessageUI(chatMessage: ChatMessage) {

    val isMessageMenuVisible = mutableStateOf(false)

    Column(
        modifier = Modifier.padding(8.dp)
    ) {

        if (chatMessage.message.type.isGroupAction()) {
            chatMessage.groupActionLabelText?.let { groupActionLabelText ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = groupActionLabelText,
                    fontWeight = FontWeight.W300,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Row(
                modifier = Modifier.padding(
                    start = if (chatMessage.isSent) chatMessage.messageUISpacerWidth.dp else 0.dp,
                    end = if (chatMessage.isSent) 0.dp else chatMessage.messageUISpacerWidth.dp
                )
            ) {

                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chatMessage.isSent) {
                            KebabMenu(
                                contentDescription = "Menu for message",
                                onClick = { isMessageMenuVisible.value = true }
                            )
                        }

                        if (chatMessage.chat.isTribe()) {
                            Text(
                                text = chatMessage.message.senderAlias?.value ?: "",
                                // TODO: Color...
                            )
                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )
                        }

                        if (chatMessage.showSendingIcon) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        if (chatMessage.showBoltIcon) {
                            Icon(
                                Icons.Default.FlashOn,
                                "Confirmed",
                                tint = Color.Green,
                                modifier = Modifier.size(24.dp).padding(4.dp)
                            )
                        }

                        if (chatMessage.showLockIcon) {
                            Icon(
                                Icons.Default.Lock,
                                "Secure chat",
                                tint = Color.LightGray,
                                modifier = Modifier.size(24.dp).padding(4.dp)
                            )
                        }
                        Text(
                            text = chatMessage.message.date.chatTimeFormat(),
                            fontWeight = FontWeight.W200,
                            textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                        )

                        if (chatMessage.isReceived) {
                            KebabMenu(
                                contentDescription = "Menu for message",
                                onClick = { isMessageMenuVisible.value = true }
                            )
                        }

                        MessageMenu(
                            chatMessage = chatMessage,
                            replyToTextAction = {
                                // TODO: Set chatMessage in the reply text...
                            },
                            isVisible = isMessageMenuVisible
                        )
                    }

                    chatMessage.message.replyMessage?.let { replyMessage ->
                        Row(
                            modifier = Modifier.height(44.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(Color.Green)
                                    .padding(16.dp),
                            )
                            // TODO: Image if available...
                            replyMessage.messageMedia?.let { media ->
                                if (media.mediaType.isImage) {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = "Image",
                                        tint = Color.Green,
                                        modifier = Modifier.size(88.dp).padding(4.dp)
                                    )
                                } else {
                                    // show
                                    Icon(
                                        Icons.Default.AttachFile,
                                        contentDescription = "Attachment",
                                        tint = Color.Green,
                                        modifier = Modifier.size(88.dp).padding(4.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.padding(
                                    start = 8.dp
                                ),
                                verticalArrangement = Arrangement.Center
                            ) {
                                replyMessage.senderAlias?.let { senderAlias ->
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = senderAlias.value,
                                        fontWeight = FontWeight.W300,
                                        textAlign = TextAlign.Start,
                                        maxLines = 1
                                    )
                                }
                                replyMessage.retrieveTextToShow()?.let { replyMessageText ->
                                    if (replyMessageText.isNotEmpty()) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = replyMessageText,
                                            fontWeight = FontWeight.W300,
                                            textAlign = TextAlign.Start,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                        // TODO: Might want a divider here....
                    }
                    chatMessage.message.messageMedia?.let { media ->
                        // TODO: Show attachment
                        if (media.mediaType.isImage) {
//                        val mediaData = chatMessage.message.retrieveUrlAndMessageMedia()
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Image",
                                tint = Color.Green,
                                modifier = Modifier.size(88.dp).padding(4.dp)
                            )
                        } else {
                            // show
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Attachment",
                                tint = Color.Green,
                                modifier = Modifier.size(88.dp).padding(4.dp)
                            )
                        }
                    }

                    if (chatMessage.isDeleted) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "This message has been deleted",
                            fontWeight = FontWeight.W300,
                            textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                        )
                    } else if (chatMessage.isFlagged) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "This message has been flagged",
                            fontWeight = FontWeight.W300,
                            textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                        )
                    } else {
                        chatMessage.message.retrieveTextToShow()?.let { messageText ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = messageText,
                                fontWeight = FontWeight.W400
                            )
                        }
                    }

                    if (chatMessage.showFailedContainer) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Go back",
                                tint = Color.Red,
                                modifier = Modifier.size(22.dp).padding(4.dp)
                            )
                            Text(
                                text = "Failed message",
                                color = Color.Red,
                                textAlign = TextAlign.Start
                            )
                        }

                    }

                    // TODO: Attachment not supported... but give download functionality...
                }

            }
        }
    }
}

@Composable
fun ChatMessageUIPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(
                vertical = 30.dp
            )
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}