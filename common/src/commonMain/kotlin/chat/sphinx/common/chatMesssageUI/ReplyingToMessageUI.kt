package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
                        tint = Color.Green,
                        modifier = Modifier.size(88.dp).padding(4.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(
                    start = 10.dp
                ),
                verticalArrangement = Arrangement.Center
            ) {
//                if(chatMessage.isDeleted.not())
                replyMessage.senderAlias?.let { senderAlias ->
                    Text(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        text = senderAlias.value.trim(),
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
                            modifier = Modifier.fillMaxWidth(0.8f),
                            text = replyMessageText,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.W400,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

}