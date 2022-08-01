package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ProfileState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.ServersUrlsHelper
import chat.sphinx.utils.createPlatformSettings
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.PrivatePhoto
import chat.sphinx.wrapper.contact.toPrivatePhoto
import chat.sphinx.wrapper.message.SphinxCallLink
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import com.russhwolf.settings.Settings

class ProfileViewModel {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val relayDataHandler = SphinxContainer.networkModule.relayDataHandlerImpl
    private val serversUrls = ServersUrlsHelper()

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    private val accountOwnerStateFlow: StateFlow<Contact?>
        get() = contactRepository.accountOwner


    var profileState: ProfileState by mutableStateOf(initialState())

    private fun initialState(): ProfileState = ProfileState()

    private inline fun setProfileState(update: ProfileState.() -> ProfileState) {
        profileState = profileState.update()
    }

    private fun loadProfile() {
        scope.launch(dispatchers.mainImmediate) {
            accountOwnerStateFlow.collect { contactOwner ->
                contactOwner?.let { owner ->
                    setProfileState {
                        copy(
                            alias = owner.alias?.value ?: "",
                            nodePubKey = owner.nodePubKey?.value ?: "",
                            routeHint = owner.routeHint?.value ?: "",
                            photoUrl = owner.photoUrl,
                            serverUrl = relayDataHandler.retrieveRelayUrl()?.value ?: "",
                            privatePhoto = toPrivatePhotoBoolean(owner.privatePhoto.value)
                        )
                    }
                }
            }
        }
    }
    init {
        loadProfile()
        loadServerUrls()
    }

    fun onPrivatePhotoSwitchChange(checked: Boolean){
        setProfileState {
            copy(
                privatePhoto = checked,
                saveButtonEnabled = true
            )
        }
    }
    fun onDefaultCallServerChange(text: String){
        setProfileState {
            copy(
                meetingServerUrl = text,
                saveButtonEnabled = true
            )
        }
    }

    fun onAliasTextChanged(text: String) {
        setProfileState {
            copy(
                alias = text,
                saveButtonEnabled = true
            )
        }
    }

    private fun setStatus(status: LoadResponse<Any, ResponseError>?) {
        setProfileState {
            copy(
                status = status
            )
        }
    }

    fun updateOwnerDetails(){
        scope.launch(dispatchers.mainImmediate) {
            contactRepository.updateOwner(
                alias = profileState.alias,
                privatePhoto = profileState.privatePhoto?.toPrivatePhoto(),
                tipAmount = null
            ).let { loadResponse ->
                setStatus(loadResponse)
            }

            serversUrls.setMeetingServer(profileState.meetingServerUrl)
        }
    }

    private fun loadServerUrls(){
        val meetingServer = serversUrls.getMeetingServer()

        setProfileState {
            copy(
                meetingServerUrl = meetingServer
            )
        }
    }

    private fun toPrivatePhotoBoolean(privatePhoto: Int?) : Boolean = privatePhoto == 1

}