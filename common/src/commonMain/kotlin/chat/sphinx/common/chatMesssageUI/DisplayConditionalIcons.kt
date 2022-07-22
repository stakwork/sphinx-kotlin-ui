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
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.chatTimeFormat
import com.example.compose.place_holder_text

@Composable
fun DisplayConditionalIcons(
    chatMessage: ChatMessage
) {
    val color = chatMessage.colors[chatMessage.message.id.value]

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
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
    }

    if (chatMessage.showSendingIcon) {
        CircularProgressIndicator(
            modifier = Modifier.width(20.dp).height(16.dp).padding(4.dp, 2.dp),
            color = MaterialTheme.colorScheme.tertiary,
            strokeWidth = 2.dp
        )
    }

    if (chatMessage.showBoltIcon) {
        Icon(
            Icons.Default.FlashOn,
            "Confirmed",
            tint = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(12.dp)
        )
    }

    if (chatMessage.showLockIcon && chatMessage.isSent) {
        Icon(
            Icons.Default.Lock,
            "Secure chat",
            tint = place_holder_text,
            modifier = Modifier.size(18.dp).padding(4.dp)
        )
    }

    Text(
        text = chatMessage.message.date.chatTimeFormat(),
        fontWeight = FontWeight.W300,
        fontFamily = Roboto,
        color = place_holder_text,
        fontSize = 10.sp,
        textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
    )

    if (chatMessage.showLockIcon && chatMessage.isReceived) {
        Icon(
            Icons.Default.Lock,
            "Secure chat",
            tint = place_holder_text,
            modifier = Modifier.size(18.dp).padding(4.dp)
        )
    }
}