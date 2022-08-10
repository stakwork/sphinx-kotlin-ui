package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.state.JoinTribeState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.network.query.chat.model.TribeDto
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.ChatHost
import chat.sphinx.wrapper.chat.ChatUUID
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.toFileName
import chat.sphinx.wrapper.toPhotoUrl
import chat.sphinx.wrapper.tribe.TribeJoinLink
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.Path
import theme.primary_green
import theme.primary_red

class JoinTribeViewModel(
    private val tribeJoinLink: TribeJoinLink,
    val dashboardViewModel: DashboardViewModel
) {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val networkQueryChat: NetworkQueryChat = SphinxContainer.networkModule.networkQueryChat
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val chatRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).chatRepository

    var joinTribeState: JoinTribeState by mutableStateOf(initialState())

    private fun initialState(): JoinTribeState = JoinTribeState()

    private inline fun setJoinTribeState(update: JoinTribeState.() -> JoinTribeState) {
        joinTribeState = joinTribeState.update()
    }

    private var tribeInfo : TribeDto? = null

    init {
        loadTribeData()
    }
    private fun loadTribeData(){
        scope.launch(dispatchers.mainImmediate) {
            val owner = getOwner()

            networkQueryChat.getTribeInfo(
                ChatHost(tribeJoinLink.tribeHost),
                ChatUUID(tribeJoinLink.tribeUUID)
            ).collect { loadResponse ->
                when (loadResponse) {
                    is Response.Success -> {
                        loadResponse.apply {

                            tribeInfo = value

                            val hourToStake: Long = (value.escrow_millis / 60 / 60 / 1000)

                            setJoinTribeState {
                                copy(
                                    name = value.name,
                                    description = value.description,
                                    img = value.img?.toPhotoUrl(),
                                    price_to_join = value.price_to_join.toString(),
                                    price_per_message = value.price_per_message.toString(),
                                    escrow_amount = value.escrow_amount.toString(),
                                    hourToStake = hourToStake.toString(),
                                    userAlias = owner.alias?.value ?: "",
                                    myPhotoUrl = owner.photoUrl,
                                    loadingTribe = false
                                )
                            }
                        }
                    }
                    is Response.Error -> {
                        setJoinTribeState {
                            copy(
                                loadingTribe = false
                            )
                        }
                        toast("There was an error loading the tribe. Please try again later", primary_red)
                    }
                }
            }
        }
    }

    private suspend fun getOwner(): Contact {
        return contactRepository.accountOwner.value.let { contact ->
            if (contact != null) {
                contact
            } else {
                var resolvedOwner: Contact? = null
                try {
                    contactRepository.accountOwner.collect { ownerContact ->
                        if (ownerContact != null) {
                            resolvedOwner = ownerContact
                            throw Exception()
                        }
                    }
                } catch (e: Exception) {
                }
                delay(25L)

                resolvedOwner!!
            }
        }
    }

    private var joinTribeJob: Job? = null
    fun joinTribe() {
        if (joinTribeJob?.isActive == true) {
            return
        }

        joinTribeJob = scope.launch(dispatchers.mainImmediate) {
            setJoinTribeState {
                copy(
                    status = LoadResponse.Loading
                )
            }

            tribeInfo?.myAlias = joinTribeState.userAlias
            tribeInfo?.amount = joinTribeState.price_to_join.toLong()

            tribeInfo?.let { tribeDto ->
                chatRepository.joinTribe(tribeDto).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            dashboardViewModel.toggleJoinTribeWindow(false)
                        }
                        is Response.Error -> {
                            toast("There was an error joining the tribe. Please try again later", primary_red)
                        }
                    }
                }
            }
        }
    }

    fun onAliasTextChanged(text: String){
        setJoinTribeState {
            copy(
                userAlias = text
            )
        }
    }

    fun onProfilePictureChanged(filepath: Path) {
        val ext = filepath.toFile().extension
        val mediaType = MediaType.Image(MediaType.IMAGE + "/$ext")

        setJoinTribeState {
            copy(
                userPicture = AttachmentInfo(
                    filePath = filepath,
                    mediaType = mediaType,
                    fileName = filepath.name.toFileName(),
                    isLocalFile = true
                ),
                myPhotoUrl = null
            )
        }
        tribeInfo?.setProfileImageFile(filepath)
    }

    private fun toast(
        message: String,
        color: Color = primary_green,
        delay: Long = 2000L
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.toast(
                "Join Tribe",
                message,
                color.value,
                delay
            )
        }
    }
}