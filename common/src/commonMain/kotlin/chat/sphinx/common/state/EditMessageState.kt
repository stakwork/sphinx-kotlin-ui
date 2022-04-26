package chat.sphinx.common.state

import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId

data class EditMessageState(
    val messageText: String = "",
    val chatId: ChatId?,
    val contactId: ContactId? = null
) {

}