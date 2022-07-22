package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.state.ContactState
import androidx.compose.runtime.setValue
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.contact.toContactAlias
import chat.sphinx.wrapper.lightning.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect



class AddContactViewModel() : ContactViewModel() {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository

    override var contactState: ContactState by mutableStateOf(initialState())

    private fun initialState(): ContactState = ContactState()

    private inline fun setContactState(update: ContactState.() -> ContactState) {
        contactState = contactState.update()
    }

    private fun checkValidInput() {
        setStatus(null)

        val validNickname = (contactState.contactAlias.toContactAlias() != null)
        val validAddress = (contactState.lightningNodePubKey.toLightningNodePubKey() != null)
        val validRouteHint = (contactState.lightningRouteHint.isNullOrEmpty() ||
                contactState.lightningRouteHint?.toLightningRouteHint() != null)

        if (validNickname && validAddress) {
            setSaveButtonEnabled(true)
        }
        if (!validNickname || !validAddress || !validRouteHint) {
            setSaveButtonEnabled(false)
        }
    }

    override fun onNicknameTextChanged(text: String) {
        setContactState {
            copy(
                contactAlias = text
            )
        }
        checkValidInput()
    }

    override fun onAddressTextChanged(text: String) {
        text.toVirtualLightningNodeAddress()?.let { nnVirtualAddress ->
            setContactState {
                copy(
                    lightningNodePubKey = nnVirtualAddress.getPubKey()?.value ?: "",
                    lightningRouteHint = nnVirtualAddress.getRouteHint()?.value ?: ""
                )
            }
        } ?: run {
            setContactState {
                copy(
                    lightningNodePubKey = text
                )
            }
        }
        checkValidInput()
    }

    override fun onRouteHintTextChanged(text: String) {
        text.toVirtualLightningNodeAddress()?.let { nnVirtualAddress ->
            setContactState {
                copy(
                    lightningNodePubKey = nnVirtualAddress.getPubKey()?.value ?: "",
                    lightningRouteHint = nnVirtualAddress.getRouteHint()?.value ?: ""
                )
            }
        } ?: run {
            setContactState {
                copy(
                    lightningRouteHint = text
                )
            }
        }
        checkValidInput()
    }

    private fun setStatus(status: LoadResponse<Any, ResponseError>?) {
        setContactState {
            copy(
                status = status
            )
        }
    }

    private fun setSaveButtonEnabled(buttonEnabled: Boolean) {
        setContactState {
            copy(
                saveButtonEnabled = buttonEnabled
            )
        }
    }

    override fun saveContact() {
        if (saveContactJob?.isActive == true) {
            return
        }
        saveContactJob = scope.launch(dispatchers.mainImmediate) {

            contactRepository.createContact(
                ContactAlias(contactState.contactAlias),
                LightningNodePubKey(contactState.lightningNodePubKey),
                contactState.lightningRouteHint?.let { LightningRouteHint(it) }
            ).collect { loadResponse ->
                setStatus(loadResponse)
            }
        }
    }

}