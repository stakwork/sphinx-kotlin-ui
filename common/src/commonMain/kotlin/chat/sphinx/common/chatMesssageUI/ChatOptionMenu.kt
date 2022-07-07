package chat.sphinx.common.chatMesssageUI

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import chat.sphinx.common.components.MessageMenu
import chat.sphinx.common.components.chat.KebabMenu
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel

@Composable
fun ChatOptionMenu(chatMessage:ChatMessage,chatViewModel:ChatViewModel){
    val isMessageMenuVisible = remember { mutableStateOf(false) }
    if (chatMessage.isSent&&chatMessage.isDeleted.not()) {
        KebabMenu(
            contentDescription = "Menu for message",
            onClick = { isMessageMenuVisible.value = true }
        )
    }
    MessageMenu(
        chatMessage = chatMessage,
        isVisible = isMessageMenuVisible,
        chatViewModel
    )

}