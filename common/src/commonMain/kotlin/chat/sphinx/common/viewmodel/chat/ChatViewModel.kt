package chat.sphinx.common.viewmodel.chat

import androidx.paging.PagingData
import androidx.paging.map
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.database.core.MessageDbo
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.SphinxDispatchers
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.*
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.message.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Sorts and filters the provided list.
 *
 * @param [dashboardChats] if `null` uses the current, already sorted list.
 * @param [filter] the type of filtering to apply to the list. See [ChatFilter].
 * */
suspend fun ArrayList<DashboardChat>.updateDashboardChats(
    lock: Mutex,
    dispatchers: SphinxDispatchers
) {
    lock.withLock {
        val sortedDashboardChats = withContext(dispatchers.default) {
            this@updateDashboardChats.sortedByDescending { it.sortBy }
        }

        this@updateDashboardChats.clear()
        this@updateDashboardChats.addAll(sortedDashboardChats)
    }
}

abstract class ChatViewModel(
    val chatId: ChatId?
) {
    var _chatId: ChatId? = chatId
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
//    val dashboardChats: ArrayList<DashboardChat> = ArrayList()
    val messageRepository = SphinxContainer.repositoryModule.messageRepository
    val repositoryDashboard = SphinxContainer.repositoryModule.repositoryDashboard
    val contactRepository = SphinxContainer.repositoryModule.contactRepository
    val chatRepository = SphinxContainer.repositoryModule.chatRepository
    val repositoryMedia = SphinxContainer.repositoryModule.repositoryMedia
    val memeServerTokenHandler = SphinxContainer.repositoryModule.memeServerTokenHandler
    val memeInputStreamHandler = SphinxContainer.networkModule.memeInputStreamHandler

    init {
        scope.launch(dispatchers.mainImmediate) {
            getChatOrNull()?.let { chat ->
                MessageListState.screenState(
                    MessageListData.PopulatedMessageListData(
                        messageRepository.getAllMessagesToShowByChatIdPaginated(chat.id)
                            .map { pagingData: PagingData<Message> ->
                                pagingData.map { message: Message ->
                                    ChatMessage(
                                        chat,
                                        message
                                    )
                                }
                            }

                    )
                )

//                messageRepository.getAllMessagesToShowByChatId(chat.id, 20).firstOrNull()?.let { messages ->
//                    MessageListState.screenState(
//                        MessageListData.PopulatedMessageListData(
//                            messages.map { message ->
//                                ChatMessage(
//                                    chat,
//                                    message
//                                )
//                            }
//                        )
//                    )
//                }
//                delay(1000L)
//
//                messageRepository.getAllMessagesToShowByChatId(chat.id, 1000).distinctUntilChanged().collect { messages ->
//                    MessageListState.screenState(
//                        MessageListData.PopulatedMessageListData(
//                            messages.map { message ->
//                                ChatMessage(
//                                    chat,
//                                    message
//                                )
//                            }
//                        )
//                    )
//                }
            }
        }
    }

    fun getRandomHexCode(): String {
        // TODO: Randomly generate a colour.
        return "#212121"
    }

    private fun areUrlLinkPreviewsEnabled(): Boolean = false

    protected abstract val chatSharedFlow: SharedFlow<Chat?>

    suspend fun getChatOrNull(): Chat? {
        return chatId?.let { chatRepository.getChatById(it) }
    }

    protected suspend fun getChat(): Chat {

        chatSharedFlow.replayCache.firstOrNull()?.let { chat ->
            return chat
        }

        chatSharedFlow.firstOrNull()?.let { chat ->
            return chat
        }

        var chat: Chat? = null

        try {
            chatSharedFlow.collect {
                if (it != null) {
                    chat = it
                    throw Exception()
                }
            }
        } catch (e: Exception) {}
        delay(25L)

        return chat!!
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

    protected abstract suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>?

}