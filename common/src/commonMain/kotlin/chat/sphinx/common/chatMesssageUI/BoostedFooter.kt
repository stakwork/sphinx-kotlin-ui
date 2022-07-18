package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.thumbnailUrl
import chat.sphinx.wrapper.util.getInitials
import com.example.compose.badge_red
import com.example.compose.primary_green

@Composable
fun BoostedFooter(
    chatMessage: ChatMessage
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = imageResource(Res.drawable.ic_boost_green),
            contentDescription = "Boosted Icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${chatMessage.message.reactions?.sumOf { it.amount.value }} sats",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            chatMessage.message.reactions?.forEachIndexed { index, it ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .absoluteOffset(
                            calculatePosition(chatMessage.message.reactions?.size ?: 0,index), 0.dp
                        )
                ) {
                    if (index < 3) {
                        val color = chatMessage.colors[it.id.value]

                        PhotoUrlImage(
                            photoUrl = it.senderPic?.thumbnailUrl,
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape),
                            color = if (color != null) Color(color) else Color.Gray,
                            firstNameLetter = (chatMessage.contact?.alias?.value ?: it.senderAlias?.value)?.getInitials(),
                            fontSize = 9
                        )
                    }
                }
            }
        }
        if (chatMessage.message.reactions?.size ?: 0 > 3) {
            Text(
                text = "+${(chatMessage.message.reactions?.size ?: 0) - 3}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

fun calculatePosition(size: Int, index: Int): Dp {
    when (size) {
        1 ->  when(index){
            0->{
                return 0.dp
            }
        }
        2 -> when(index){
            0->{
                return -(12.5.dp)
            }
            1->{
                return 0.dp
            }
        }
        else -> when(index){
            0->{
                return -(25.dp)
            }
            1->{
                return -(12.dp)
            }
            2->{
                return 0.dp
            }
        }
    }
    return 0.dp
}
