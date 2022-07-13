package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.state.AddContactState
import androidx.compose.runtime.setValue
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.LightningRouteHint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class AddContactViewModel() {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private var saveContactJob: Job? = null

    private val _isLoading: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    private val _isSuccess: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val isSuccess: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()


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

    fun saveContact() {
        if(saveContactJob?.isActive == true){
            return
        }
        saveContactJob = scope.launch(dispatchers.mainImmediate) {

            contactRepository.createContact(
                ContactAlias(addContactState.contactAlias),
                LightningNodePubKey(addContactState.lightningNodePubKey),
                addContactState.lightningRouteHint?.let { LightningRouteHint(it) }
            ).collect { loadResponse ->
                Exhaustive@
                when (loadResponse) {
                    is LoadResponse.Loading -> {
                        _isLoading.value = true
                    }
                    is Response.Error -> {
                        _isLoading.value = false

                    }
                    is Response.Success -> {
                        _isSuccess.value = true
                        _isLoading.value = false
                    }
                }
            }
        }
    }

}