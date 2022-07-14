package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.chat.isTribe

@Composable
fun TribeHeaderMessage(chatMessage:ChatMessage) {
    // If any joined tribe will show below text
    chatMessage.groupActionLabelText?.let { groupActionLabelText ->
        Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
            Card(backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer, shape = RoundedCornerShape(16.dp)) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = groupActionLabelText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W300,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                )
            }
        }


    }
}