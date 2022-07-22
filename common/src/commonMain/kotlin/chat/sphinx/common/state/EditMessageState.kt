package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId

data class EditMessageState(
    val messageText: MutableState<String> = mutableStateOf(""),
    val price: MutableState<Long?> = mutableStateOf(null),
    val chatId: ChatId?,
    val contactId: ContactId? = null,
    val replyToMessage: MutableState<ChatMessage?> = mutableStateOf(null)
) {

}