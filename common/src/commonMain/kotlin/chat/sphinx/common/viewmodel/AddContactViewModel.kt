package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import chat.sphinx.common.state.AddContactState
import androidx.compose.runtime.setValue
import chat.sphinx.concepts.notification.SphinxNotificationManager
import chat.sphinx.concepts.repository.contact.ContactRepository
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.LightningRouteHint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AddContactViewModel() {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private var saveContactJob: Job? = null


    var addContactState: AddContactState by mutableStateOf(initialState())
        private set

    private fun initialState(): AddContactState = AddContactState()

    private inline fun setAddContactState(update: AddContactState.() -> AddContactState) {
        addContactState = addContactState.update()
    }

    fun onNicknameTextChanged(text: String) {
        setAddContactState {
            copy(
                contactAlias = text
            )
        }
    }
    fun onAddressTextChanged(text: String) {
        setAddContactState {
            copy(
                lightningNodePubKey = text
            )
        }
    }
    fun onRouteHintTextChanged(text: String) {
        setAddContactState {
            copy(
                lightningRouteHint = text
            )
        }
    }

    fun saveContact(){
        if(saveContactJob?.isActive == true){
            return
        }
        saveContactJob = scope.launch(dispatchers.mainImmediate) {

            contactRepository.createContact(
                ContactAlias(addContactState.contactAlias),
                LightningNodePubKey(addContactState.lightningNodePubKey),
                addContactState.lightningRouteHint?.let { LightningRouteHint(it) }
            )
        }
    }

}