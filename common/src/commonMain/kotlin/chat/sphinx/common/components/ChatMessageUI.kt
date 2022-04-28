package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.chatTimeFormat
import chat.sphinx.wrapper.message.isFailed
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
fun ChatMessageUI(chatMessage: ChatMessage) {

    Column(
        modifier = Modifier.padding(8.dp)
    ) {

        Row {
            if (chatMessage.isSent) {
                Spacer(
                    modifier = Modifier.width(chatMessage.messageUISpacerWidth.dp)
                )
            }
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                }

                chatMessage.message.retrieveTextToShow()?.let { messageText ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = messageText,
                        fontWeight = FontWeight.W400,
                        textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                    )
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

            if (chatMessage.isReceived) {
                Spacer(
                    modifier = Modifier.width(chatMessage.messageUISpacerWidth.dp)
                )
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