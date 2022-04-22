package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.chatTimeFormat
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

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = chatMessage.message.date.chatTimeFormat(),
                    fontWeight = FontWeight.W200,
                    textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                )
                chatMessage.message.retrieveTextToShow()?.let { messageText ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = messageText,
                        fontWeight = FontWeight.W400,
                        textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                    )
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