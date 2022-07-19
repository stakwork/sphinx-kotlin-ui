package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.MessageMediaImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.linkify.LinkTag
import chat.sphinx.utils.toAnnotatedString
import chat.sphinx.wrapper.message.MessageType
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
fun ChatCard(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    val uriHandler = LocalUriHandler.current
    val receiverCorner =
        RoundedCornerShape(topEnd = 10.dp, topStart = 0.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
    val senderCorner =
        RoundedCornerShape(topEnd = 0.dp, topStart = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp)

    Card(
        backgroundColor = if (chatMessage.isReceived) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.inversePrimary,
        shape = if (chatMessage.isReceived) receiverCorner else senderCorner
    ) {
        Box(modifier = Modifier) {
            Column {
                chatMessage.message.replyMessage?.let { _ ->
                    ReplyingToMessageUI(
                        chatMessage,
                        chatViewModel
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Divider()
                }
                chatMessage.message.messageMedia?.let { media ->
                    Column(modifier = Modifier.padding( if(media.mediaType.isImage) 0.dp else 12.dp)) {
                        if (media.mediaType.isImage) {
                            chatMessage.message.messageMedia?.let { messageMedia ->
                                MessageMediaImage(
                                    chatMessage.message,
                                    messageMedia = messageMedia,
                                    chatViewModel = chatViewModel,
                                    modifier = Modifier.wrapContentHeight().fillMaxWidth()
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Attachment",
                                tint = Color.Green,
                                modifier = Modifier.size(88.dp).padding(4.dp)
                            )
                        }
                    }
                }
                with(chatMessage.message){
                    if(this.retrieveTextToShow().isNullOrEmpty().not()||this.reactions?.isNotEmpty()==true)
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (chatMessage.isFlagged) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "This message has been flagged",
                                    fontWeight = FontWeight.W300,
                                    textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                                )
                            } else {
                                chatMessage.message.retrieveTextToShow()?.let { messageText ->
                                    Row(
                                        modifier = Modifier.wrapContentWidth(if (chatMessage.isSent) Alignment.End else Alignment.Start),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val annotatedString = messageText.toAnnotatedString()
                                        ClickableText(
                                            annotatedString,
                                            style = TextStyle(
                                                fontWeight = FontWeight.W400,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 13.sp
                                            ),
                                            onClick = { offset ->
                                                annotatedString.getStringAnnotations(
                                                    start = offset,
                                                    end = offset
                                                ).firstOrNull()?.let { annotation ->
                                                    when (annotation.tag) {
                                                        LinkTag.WebURL.name -> {
                                                            uriHandler.openUri(annotation.item)
                                                        }
                                                        LinkTag.BitcoinAddress.name -> {
                                                            val bitcoinUriScheme =
                                                                if (annotation.item.startsWith("bitcoin:")) "bitcoin:" else ""
                                                            val bitcoinURI =
                                                                "$bitcoinUriScheme${annotation.item}"

                                                            uriHandler.openUri(bitcoinURI)
                                                        }
                                                    }
                                                }
                                            }
                                        )

                                        // TODO: Make clickable text compatible with selectable text...
                                        //                                SelectionContainer {
                                        //
                                        //                                }
                                    }

                                }
                                if (chatMessage.message.type == MessageType.BotRes) {
                                    chatMessage.message.messageContentDecrypted?.let {
                                        val annotatedString = it.value.toAnnotatedString()
                                        ClickableText(
                                            annotatedString,
                                            style = TextStyle(
                                                fontWeight = FontWeight.W400,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 13.sp
                                            ),
                                            onClick = { offset ->
                                                annotatedString.getStringAnnotations(
                                                    start = offset,
                                                    end = offset
                                                ).firstOrNull()?.let { annotation ->
                                                    when (annotation.tag) {
                                                        LinkTag.WebURL.name -> {
                                                            uriHandler.openUri(annotation.item)
                                                        }
                                                        LinkTag.BitcoinAddress.name -> {
                                                            val bitcoinUriScheme =
                                                                if (annotation.item.startsWith("bitcoin:")) "bitcoin:" else ""
                                                            val bitcoinURI =
                                                                "$bitcoinUriScheme${annotation.item}"

                                                            uriHandler.openUri(bitcoinURI)
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
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
                            chatMessage.boostsLayoutState?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                BoostedFooter(it)
                            }
                        }
                }
                // TODO: Attachment not supported... but give download functionality...
            }
        }
    }

}