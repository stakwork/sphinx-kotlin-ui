package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import okio.Path

data class EditMessageState(
    val messageText: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(text = "")),
    val price: MutableState<Long?> = mutableStateOf(null),
    val attachmentInfo: MutableState<AttachmentInfo?> = mutableStateOf(null),
    val chatId: ChatId?,
    val contactId: ContactId? = null,
    val replyToMessage: MutableState<ChatMessage?> = mutableStateOf(null)
)