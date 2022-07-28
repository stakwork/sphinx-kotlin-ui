package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.CustomDivider
import chat.sphinx.common.components.MessageAudio
import chat.sphinx.common.components.MessageFile
import chat.sphinx.common.components.MessageMediaImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.linkify.LinkTag
import chat.sphinx.utils.toAnnotatedString
import chat.sphinx.wrapper.message.media.isAudio
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.message.media.isPdf
import chat.sphinx.wrapper.message.media.isUnknown
import chat.sphinx.wrapper.message.retrieveTextToShow
import com.example.compose.badge_red
import com.example.compose.light_divider

@Composable
fun ChatCard(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    modifier: Modifier? = null,
) {
    val uriHandler = LocalUriHandler.current
    val receiverCorner =
        RoundedCornerShape(topEnd = 10.dp, topStart = 0.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
    val senderCorner =
        RoundedCornerShape(topEnd = 0.dp, topStart = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp)

    Card(
        backgroundColor = if (chatMessage.isReceived) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.inversePrimary,
        shape = if (chatMessage.isReceived) receiverCorner else senderCorner,
        modifier = modifier ?: Modifier
    ) {
        val density = LocalDensity.current
        var rowWidth by remember { mutableStateOf(0.dp) }

        Column(modifier = Modifier.onSizeChanged {
            rowWidth = with(density) { it.width.toDp() }
        }) {
            chatMessage.message.replyMessage?.let { _ ->
                ReplyingToMessageUI(
                    chatMessage,
                    chatViewModel
                )
                Spacer(modifier = Modifier.height(4.dp))
                CustomDivider(color = light_divider, modifier = Modifier.width(rowWidth))
            }
            chatMessage.message.messageMedia?.let { media ->
                Column(modifier = Modifier.padding( if (media.mediaType.isImage) 0.dp else 12.dp) ) {
                    if (media.mediaType.isImage) {
                        chatMessage.message.messageMedia?.let { messageMedia ->
                            MessageMediaImage(
                                chatMessage.message,
                                messageMedia = messageMedia,
                                chatViewModel = chatViewModel,
                                modifier = Modifier.wrapContentHeight().fillMaxWidth()
                            )
                        }
                    } else if (media.mediaType.isUnknown || media.mediaType.isPdf) {
                        MessageFile(
                            chatMessage = chatMessage,
                            chatViewModel = chatViewModel,
                        )
//                    } else if (media.mediaType.isAudio) {
//                        MessageAudio(
//                            chatMessage = chatMessage,
//                            chatViewModel = chatViewModel,
//                        )
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
                if (
                    this.retrieveTextToShow().isNullOrEmpty().not() ||
                    this.reactions?.isNotEmpty() == true
                )
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (chatMessage.message.messageDecryptionError) {
                            Text(
                                modifier = Modifier.wrapContentWidth(if (chatMessage.isSent) Alignment.End else Alignment.Start),
                                text = "DECRYPTION ERROR",
                                fontWeight = FontWeight.W300,
                                fontFamily = Roboto,
                                fontSize = 13.sp,
                                color = badge_red
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
                                            fontSize = 13.sp,
                                            fontFamily = Roboto,
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
                            BoostedFooter(it, modifier = Modifier.width(
                                maxOf(rowWidth - 24.dp, 200.dp)
                            ))
                        }
                    }
            }
            // TODO: Attachment not supported... but give download functionality...
        }
    }
}