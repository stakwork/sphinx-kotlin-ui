package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.MessageMediaImage
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.chat.isTribe
import utils.conditional

@Composable
fun DirectPaymentUI(chatMessage:ChatMessage,chatViewModel: ChatViewModel) {
    val receiverCorner =
        RoundedCornerShape(
            topEnd = 10.dp,
            topStart = 0.dp,
            bottomEnd = 10.dp,
            bottomStart = 10.dp
        )
    val senderCorner =
        RoundedCornerShape(
            topEnd = 0.dp,
            topStart = 10.dp,
            bottomEnd = 10.dp,
            bottomStart = 10.dp
        )
    Box() {
        Card(
            backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = if (chatMessage.isReceived) receiverCorner else senderCorner,
            modifier = Modifier.fillMaxWidth(0.25f).conditional(chatMessage.message.messageContentDecrypted?.value?.isEmpty()
                ?.not() == true){ Modifier.fillMaxWidth(0.3f)}
        ) {
            Column(horizontalAlignment = if (chatMessage.isSent) Alignment.End else Alignment.Start) {
                Row(
                    horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(chatMessage.chat.isTribe()){
                        Box(modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp)){
                            PhotoUrlImage(
                                photoUrl = chatMessage.message.recipientPic,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(
                                        CircleShape
                                    ))
                        }
                    }
                    if (chatMessage.isReceived) {

                        Image(
                            painter = imageResource(Res.drawable.ic_received),
                            contentDescription = "Sent Icon",
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.inverseSurface)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        chatMessage.message.amount.value.toString(),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "sats",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (chatMessage.isSent)
                        Image(
                            painter = imageResource(Res.drawable.ic_sent),
                            contentDescription = "Sent Icon",
                            modifier = Modifier.size(20.dp)
                        )


                }
                if (chatMessage.message.messageContentDecrypted?.value?.isEmpty()
                        ?.not() == true
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 12.dp, end = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            chatMessage.message.messageContentDecrypted?.value ?: "",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                chatMessage.message.messageMedia?.let {
                    Row(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                    ) {

                        MessageMediaImage(
                            chatMessage.message,
                            messageMedia = it,
                            chatViewModel = chatViewModel,
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                }
                if( chatMessage.message.messageMedia==null)
                    Spacer(modifier = Modifier.height(16.dp))
            }

        }

    }
}