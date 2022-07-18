package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ContactState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditContactViewModel : ContactViewModel() {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private var editContactJob: Job? = null


    override var contactState: ContactState by mutableStateOf(initialState())

    private fun initialState(): ContactState = ContactState()

    private inline fun setContactState(update: ContactState.() -> ContactState) {
        contactState = contactState.update()
    }


    override fun onNicknameTextChanged(text: String) {
        TODO("Not yet implemented")
    }

    override fun onAddressTextChanged(text: String) {
        TODO("Not yet implemented")
    }


    override fun onRouteHintTextChanged(text: String) {
        TODO("Not yet implemented")
    }

    override fun saveContact() {
        TODO("Not yet implemented")
    }

}