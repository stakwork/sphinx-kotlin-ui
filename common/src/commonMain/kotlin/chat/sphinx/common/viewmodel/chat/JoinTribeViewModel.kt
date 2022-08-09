package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.JoinTribeState
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.network.query.chat.model.TribeDto
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.ChatHost
import chat.sphinx.wrapper.chat.ChatUUID
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.tribe.TribeJoinLink
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class JoinTribeViewModel() {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val networkQueryChat: NetworkQueryChat = SphinxContainer.networkModule.networkQueryChat
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val chatRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).chatRepository


    private val accountOwnerStateFlow: StateFlow<Contact?>
        get() = contactRepository.accountOwner

    var joinTribeState: JoinTribeState by mutableStateOf(initialState())

    private fun initialState(): JoinTribeState = JoinTribeState()

    private inline fun setJoinTribeState(update: JoinTribeState.() -> JoinTribeState) {
        joinTribeState = joinTribeState.update()
    }

    private var tribeInfo : TribeDto? = null
    var tribeJoinLink: TribeJoinLink? = null

    init {
        loadTribeData()
    }

    fun loadTribeData(tribeJoinLink: TribeJoinLink?){
        this.tribeJoinLink = tribeJoinLink

        loadTribeData()
    }

    private fun loadTribeData(){
        tribeJoinLink?.let { tribeJoinLink ->
            scope.launch(dispatchers.mainImmediate) {
                accountOwnerStateFlow.collect { contactOwner ->
                    contactOwner?.let { owner ->
                        networkQueryChat.getTribeInfo(
                            ChatHost(tribeJoinLink.tribeHost),
                            ChatUUID(tribeJoinLink.tribeUUID)
                        ).collect { loadResponse ->
                            when (loadResponse) {
                                is Response.Success -> {
                                    tribeInfo = loadResponse.value

                                    val hourToStake: Long = (loadResponse.value.escrow_millis / 60 / 60 / 1000)

                                    loadResponse.apply {
                                        setJoinTribeState {
                                            copy(
                                                name = value.name,
                                                description = value.description,
                                                img = PhotoUrl(value.img.toString()),
                                                tags = value.tags,
                                                group_key = value.group_key,
                                                owner_pubkey = value.owner_pubkey,
                                                owner_route_hint = value.owner_route_hint,
                                                owner_alias = value.owner_alias,
                                                price_to_join = value.price_to_join.toString(),
                                                price_per_message = value.price_per_message.toString(),
                                                escrow_amount = value.escrow_amount.toString(),
                                                hourToStake = hourToStake.toString(),
                                                unlisted = value.unlisted,
                                                private = value.private,
                                                deleted = value.deleted,
                                                app_url = value.app_url,
                                                feed_url = value.feed_url,
                                                feed_type = feed_type,
                                                userAlias = owner.alias?.value ?: "",
                                                userPhotoUrl = owner.photoUrl
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
                chatRepository.joinTribe(tribeDto).collect {
                    it
                }
            }
        }
    }

}