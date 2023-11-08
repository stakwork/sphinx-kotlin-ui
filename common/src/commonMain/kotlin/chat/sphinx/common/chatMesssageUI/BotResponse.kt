package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.isPaidTextMessage

@Composable
fun BotResponse(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    chatMessage.botResponse?.let { contentHtml ->

        // Extracting the text from the HTML and reformatting it
        val formattedText = contentHtml
            .replace(Regex("<div style=\"[^\"]*\"><div style=\"[^\"]*\">"), "\n") // Matches the start of a label
            .replace(Regex("</div><div style=\"[^\"]*\">"), "\n") // Matches the start of a command
            .replace("</div>", "") // Matches the end of a command
            .replace(Regex("<.*?>"), "") // Removes all other HTML tags
            .trim()

        val topPadding = if (chatMessage.message.isPaidTextMessage && chatMessage.isSent) 44.dp else 12.dp

        Row(
            modifier = Modifier
                .padding(12.dp, topPadding, 12.dp, 12.dp)
                .wrapContentWidth(if (chatMessage.isSent) Alignment.End else Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                formattedText,
                style = TextStyle(
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 13.sp,
                    fontFamily = Roboto,
                )
            )
        }
    }
}
