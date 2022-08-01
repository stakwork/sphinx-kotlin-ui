package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.MessageMediaImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.message.retrieveTextToShow
import chat.sphinx.wrapper.chat.isTribe
import theme.wash_out_received
import theme.wash_out_send

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReplyingToMessageUI(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    chatMessage.message.replyMessage?.let { replyMessage ->
        val color = if (chatMessage.colors[replyMessage.id.value] != null) {
            Color(chatMessage.colors[replyMessage.id.value]!!)
        } else {
            Color.Gray
        }

        Row(
            modifier = Modifier.height(44.dp).padding(top = 8.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(color),
            )
            replyMessage.messageMedia?.let { media ->
                if (media.mediaType.isImage) {
                    MessageMediaImage(
                        chatMessage.message,
                        media,
                        chatViewModel,
                        modifier = Modifier.height(30.dp).width(40.dp).padding(start = 10.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = "Attachment",
                        tint = Color.Gray,
                        modifier = Modifier.height(88.dp).padding(start = 10.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(
                    start = 10.dp
                ),
                verticalArrangement = Arrangement.Center
            ) {
                val alias = if (chatMessage.chat.isTribe()) {
                    replyMessage.senderAlias?.value
                } else {
                    if (replyMessage.sender == chatMessage.accountOwner().id) {
                        chatMessage.accountOwner().alias?.value
                    } else {
                        chatMessage.contact?.alias?.value
                    }
                }

                alias?.let { senderAlias ->
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = senderAlias.trim(),
                        fontFamily = Roboto,
                        fontWeight = FontWeight.W600,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                replyMessage.retrieveTextToShow()?.let { replyMessageText ->
                    if (replyMessageText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            text = replyMessageText,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.W400,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            fontSize = 11.sp,
                            color = if (chatMessage.isSent) wash_out_send else wash_out_received,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}