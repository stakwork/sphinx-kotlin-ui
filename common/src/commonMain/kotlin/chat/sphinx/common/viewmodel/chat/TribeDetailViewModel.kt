package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.TribeDetailState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.chat.ChatAlias
import chat.sphinx.wrapper.chat.isTribeOwnedByAccount
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.eeemmddhmma
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class TribeDetailViewModel() {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val chatRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).chatRepository
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository

    private var detailChatId: ChatId? = null


    private val accountOwnerStateFlow: StateFlow<Contact?>
        get() = contactRepository.accountOwner

    init {
        loadTribeDetail()
    }

    fun loadTribeDetail(chatId: ChatId){
        this.detailChatId = chatId

        loadTribeDetail()

    }

    private fun loadTribeDetail(){
        scope.launch(dispatchers.mainImmediate){
            detailChatId?.let {
                accountOwnerStateFlow.collect { contactOwner ->
                    contactOwner?.let { owner ->
                        chatRepository.getChatById(it)?.let { chat ->

                            val tribeOwner = chat.isTribeOwnedByAccount(owner.nodePubKey)

                            setTribeDetailState {
                                copy(
                                    tribeName = chat.name?.value ?: "",
                                    tribePhotoUrl = chat.photoUrl,
                                    createDate = "Created on ${chat.createdAt.eeemmddhmma()}",
                                    tribeConfigurations =
                                    "Price per message: ${chat.pricePerMessage?.value ?: 0L} sat" +
                                            " - Amount to stake: ${chat.escrowAmount?.value ?: 0L} sat ",
                                    userAlias = chat.myAlias?.value ?: "",
                                    myPhotoUrl = chat.myPhotoUrl ?: owner.photoUrl,
                                    tribeOwner = tribeOwner
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    fun onAliasTextChanged(text: String){
        setTribeDetailState {
            copy(
                userAlias = text,
                saveButtonEnable = true
            )

        }
    }

    fun updateProfileAlias(){
        scope.launch(dispatchers.mainImmediate) {
            detailChatId?.let { chatId ->
                chatRepository.updateChatProfileInfo(
                    chatId,
                    ChatAlias(tribeDetailState.userAlias)
                )
            }.let {

            }
        }
    }

    var tribeDetailState: TribeDetailState by mutableStateOf(initialTribeDetailState())

    private fun initialTribeDetailState(): TribeDetailState = TribeDetailState()

    private inline fun setTribeDetailState(update: TribeDetailState.() -> TribeDetailState) {
        tribeDetailState = tribeDetailState.update()
    }
}