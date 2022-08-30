package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.BubbleBackground
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.chatTimeFormat
import theme.place_holder_text

@Composable
fun DisplayConditionalIcons(
    chatMessage: ChatMessage
) {
    if (
        chatMessage.background !is BubbleBackground.First
    ) {
        return
    }

    val color = chatMessage.colors[chatMessage.message.id.value]

    Row(
        modifier = Modifier
            .height(15.dp)
            .padding(bottom = 2.dp, end = if (chatMessage.isSent) 5.dp else 0.dp, start = if (chatMessage.isSent) 0.dp else 5.dp)
    ) {
        if (
            chatMessage.chat.isTribe() &&
            chatMessage.isReceived &&
            (chatMessage.isDeleted.not() && chatMessage.isFlagged.not())
        ) {
            Text(
                text = chatMessage.message.senderAlias?.value ?: "",
                color = if (color != null) Color(color) else Color.Unspecified,
                fontSize = 10.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
            )
            Spacer(
                modifier = Modifier.width(4.dp)
            )
        }

        if (chatMessage.showSendingIcon) {
            CircularProgressIndicator(
                modifier = Modifier.height(14.dp).width(14.dp).padding(end = 4.dp, bottom = 2.dp),
                color = MaterialTheme.colorScheme.tertiary,
                strokeWidth = 2.dp
            )
        }

        if (chatMessage.showBoltIcon) {
            Icon(
                Icons.Default.FlashOn,
                "Confirmed",
                tint = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.height(14.dp).width(13.dp).padding(bottom = 1.dp)
            )
        }

        if (chatMessage.showLockIcon && chatMessage.isSent) {
            Icon(
                Icons.Default.Lock,
                "Secure chat",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.height(14.dp).width(13.dp).padding(end = 1.dp, bottom = 2.dp)
            )
        }

        Text(
            text = chatMessage.message.date.chatTimeFormat(),
            fontWeight = FontWeight.W400,
            fontFamily = Roboto,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 10.sp,
            textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
        )

        if (chatMessage.showLockIcon && chatMessage.isReceived) {
            Icon(
                Icons.Default.Lock,
                "Secure chat",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.height(14.dp).width(13.dp).padding(end = 1.dp, bottom = 2.dp)
            )
        }
    }
}