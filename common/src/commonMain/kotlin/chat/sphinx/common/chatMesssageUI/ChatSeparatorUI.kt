package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.before

@Composable
fun DateSeparator(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    val dateMessage = chatMessage.message.date
    val date = when {
        DateTime.getToday00().before(dateMessage) -> {
            "Today"
        }
        else -> {
            DateTime.getFormatMMMEEEdd().format(dateMessage.value)
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                color = Color.Gray,
                text = date,
                fontFamily = Roboto
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color.Gray
            )
        }
    }
}