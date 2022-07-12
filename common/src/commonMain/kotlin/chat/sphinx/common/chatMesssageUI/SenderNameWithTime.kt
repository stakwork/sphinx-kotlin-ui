package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
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
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
fun SenderNameWithTime(chatMessage: ChatMessage, color: Color) {
    chatMessage.message.replyMessage?.let { replyMessage ->

        Row(
            modifier = Modifier.height(44.dp).padding(top = 8.dp, start = 8.dp, end = 8.dp,),
            horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(color)
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
                    start = 10.dp
                ),
                verticalArrangement = Arrangement.Center
            ) {
                if(chatMessage.isDeleted.not())
                    replyMessage.senderAlias?.let { senderAlias ->
                        Text(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            text = senderAlias.value.trim(),
                            fontWeight = FontWeight.W300,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                replyMessage.retrieveTextToShow()?.let { replyMessageText ->
                    if (replyMessageText.isNotEmpty()) {
                        Text(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            text = replyMessageText,
                            fontWeight = FontWeight.W300,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

}