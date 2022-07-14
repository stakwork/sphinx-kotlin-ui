package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.state.AddContactState
import androidx.compose.runtime.setValue
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.contact.toContactAlias
import chat.sphinx.wrapper.lightning.*
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
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private var saveContactJob: Job? = null


    var addContactState: AddContactState by mutableStateOf(initialState())
        private set

    private fun initialState(): AddContactState = AddContactState()

    private inline fun setAddContactState(update: AddContactState.() -> AddContactState) {
        addContactState = addContactState.update()
    }

    private fun checkValidInput() {
        setStatus(null)

        val validNickname = (addContactState.contactAlias.toContactAlias() != null)
        val validAddress = (addContactState.lightningNodePubKey.toLightningNodePubKey() != null)
        val validRouteHint = (addContactState.lightningRouteHint.isNullOrEmpty() ||
                addContactState.lightningRouteHint?.toLightningRouteHint() != null)

        if (validNickname && validAddress) {
            setSaveButtonEnabled(true)
        }
        if (!validNickname || !validAddress || !validRouteHint) {
            setSaveButtonEnabled(false)
        }
    }

    fun onNicknameTextChanged(text: String) {
        setAddContactState {
            copy(
                contactAlias = text
            )
        }
        checkValidInput()
    }

    fun onAddressTextChanged(text: String) {
        text.toVirtualLightningNodeAddress()?.let { nnVirtualAddress ->
            setAddContactState {
                copy(
                    lightningNodePubKey = nnVirtualAddress.getPubKey()?.value ?: "",
                    lightningRouteHint = nnVirtualAddress.getRouteHint()?.value ?: ""
                )
            }
        } ?: run {
            setAddContactState {
                copy(
                    lightningNodePubKey = text
                )
            }
        }
        checkValidInput()
    }

    fun onRouteHintTextChanged(text: String) {
        text.toVirtualLightningNodeAddress()?.let { nnVirtualAddress ->
            setAddContactState {
                copy(
                    lightningNodePubKey = nnVirtualAddress.getPubKey()?.value ?: "",
                    lightningRouteHint = nnVirtualAddress.getRouteHint()?.value ?: ""
                )
            }
        } ?: run {
            setAddContactState {
                copy(
                    lightningRouteHint = text
                )
            }
        }
        checkValidInput()
    }

    private fun setStatus(status: LoadResponse<Any, ResponseError>?) {
        setAddContactState {
            copy(
                status = status
            )
        }
    }

    private fun setSaveButtonEnabled(buttonEnabled: Boolean) {
        setAddContactState {
            copy(
                saveButtonEnabled = buttonEnabled
            )
        }
    }

    fun saveContact() {
        if (saveContactJob?.isActive == true) {
            return
        }
        saveContactJob = scope.launch(dispatchers.mainImmediate) {

            contactRepository.createContact(
                ContactAlias(addContactState.contactAlias),
                LightningNodePubKey(addContactState.lightningNodePubKey),
                addContactState.lightningRouteHint?.let { LightningRouteHint(it) }
            ).collect { loadResponse ->
                setStatus(loadResponse)
            }
        }
    }

}