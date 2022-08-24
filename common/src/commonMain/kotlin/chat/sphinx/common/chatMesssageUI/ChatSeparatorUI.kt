package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.separatorTimeFormat

@Composable
fun DateSeparator(
    chatMessage: ChatMessage
) {
    val dateMessage = chatMessage.message.date.separatorTimeFormat()

    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                color = MaterialTheme.colorScheme.onBackground,
                text = dateMessage,
                fontFamily = Roboto,
                fontSize = 11.sp
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}