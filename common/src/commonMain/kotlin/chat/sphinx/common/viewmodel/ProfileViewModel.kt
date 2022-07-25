package chat.sphinx.common.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ContactState
import chat.sphinx.common.state.ProfileState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.message.SphinxCallLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class ProfileViewModel(val dashboardViewModel: DashboardViewModel) {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    val accountOwnerStateFlow: StateFlow<Contact?>
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
            copy(privatePhoto = checked)
        }
    }
    fun onDefaultCallServerChange(text: String){
        setProfileState {
            copy(defaultCallServer = text)
        }
    }

    private fun loadServerUrls(){
        val callServerUrl = SphinxCallLink.CALL_SERVER_URL_KEY
        val defaultCallServer = SphinxCallLink.DEFAULT_CALL_SERVER_URL
        setProfileState {
            copy(
                defaultCallServer = defaultCallServer
            )
        }
    }

    private fun toPrivatePhotoBoolean(privatePhoto: Int?) : Boolean = privatePhoto != 1

}