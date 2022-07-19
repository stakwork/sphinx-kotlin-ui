package chat.sphinx.common.state

import chat.sphinx.wrapper.dashboard.ContactId

sealed class ContactScreenState {
    object Choose : ContactScreenState()
    object NewToSphinx : ContactScreenState()
    object AlreadyOnSphinx : ContactScreenState()
    data class EditContact(val contactId: ContactId) : ContactScreenState()



}