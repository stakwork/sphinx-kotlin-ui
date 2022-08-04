package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.message.MessageUUID

data class ChatPaymentState(
    val message: String = "",
    val amount: Long? = null,
    val chatId: ChatId? = null,
    val contactId: ContactId? = null,
    val messageUUID: MessageUUID? = null,
    val status: LoadResponse<Any, ResponseError>? = null,
    val saveButtonEnabled: Boolean = false,
)