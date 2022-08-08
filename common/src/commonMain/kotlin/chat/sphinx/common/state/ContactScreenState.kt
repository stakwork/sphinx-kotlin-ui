package chat.sphinx.common.state

import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.lightning.LightningNodeDescriptor

sealed class ContactScreenState {
    object Choose : ContactScreenState()
    object NewToSphinx : ContactScreenState()
    data class AlreadyOnSphinx(
        val pubKey: LightningNodeDescriptor? = null
    ) : ContactScreenState()
    data class EditContact(
        val contactId: ContactId? = null
    ) : ContactScreenState()

}