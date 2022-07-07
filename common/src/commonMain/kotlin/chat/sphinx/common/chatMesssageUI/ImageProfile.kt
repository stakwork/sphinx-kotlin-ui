package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage

@Composable
fun ImageProfile(
    chatMessage: ChatMessage,
    color: Color
) {
    PhotoUrlImage(
        chatMessage.contact?.photoUrl ?: chatMessage.message.senderPic,
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape),
        color = color,
        firstNameLetter = chatMessage.message.senderAlias?.value?.split("")?.get(1)
    )
}
