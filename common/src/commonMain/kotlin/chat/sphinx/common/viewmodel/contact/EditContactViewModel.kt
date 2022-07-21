package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ContactState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.lightning.toLightningRouteHint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EditContactViewModel(private val contactId: ContactId?) : ContactViewModel() {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val subscriptionRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).subscriptionRepository


    override var contactState: ContactState by mutableStateOf(initialState())

    private fun initialState(): ContactState = ContactState()

    private inline fun setContactState(update: ContactState.() -> ContactState) {
        contactState = contactState.update()
    }
    init {
        initContactDetails()
    }
    private fun initContactDetails() {

        scope.launch(dispatchers.mainImmediate) {
            if (contactId != null) {
                contactRepository.getContactById(contactId).firstOrNull()?.let { contact ->
                    contact.nodePubKey?.let { lightningNodePubKey ->

                        val subscription = subscriptionRepository.getActiveSubscriptionByContactId(
                            contactId
                        ).firstOrNull()

                        setContactState {
                            copy(
                                contactAlias = contact.alias?.value ?: "",
                                lightningNodePubKey = contact.nodePubKey?.value ?: "",
                                lightningRouteHint = contact.routeHint?.value ?: "",
                                photoUrl = contact.photoUrl,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNicknameTextChanged(text: String) {
        setContactState {
            copy(
                contactAlias = text,
                saveButtonEnabled = true
            )
        }
    }

    override fun onAddressTextChanged(text: String) {
        TODO("Not yet implemented")
    }


    override fun onRouteHintTextChanged(text: String) {
        TODO("Not yet implemented")
    }

    private fun setStatus(status: LoadResponse<Any, ResponseError>?) {
        setContactState {
            copy(
                status = status
            )
        }
    }

    override fun saveContact() {
        if (saveContactJob?.isActive == true) {
            return
        }
        saveContactJob = scope.launch(dispatchers.mainImmediate) {

            if (contactId != null) {
                contactRepository.updateContact(
                    contactId,
                    ContactAlias(contactState.contactAlias),
                    contactState.lightningRouteHint?.toLightningRouteHint()
                ).let { loadResponse ->
                    setStatus(loadResponse)
                }
            }
        }
    }

}