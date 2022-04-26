package chat.sphinx.common.viewmodel.chat

import androidx.paging.PagingData
import androidx.paging.map
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.concepts.repository.message.model.SendMessage
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.SphinxDispatchers
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.*
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.message.*
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
            }
        }
    }

    fun getRandomHexCode(): String {
        // TODO: Randomly generate a colour.
        return "#212121"
    }

    protected abstract val chatSharedFlow: SharedFlow<Chat?>

    suspend fun getChatOrNull(): Chat? {
        return chatId?.let { chatRepository.getChatById(it) }
    }

    protected abstract suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>?

    // Message sending logic...
    abstract var editMessageState: EditMessageState
        protected set

    abstract fun initialState(): EditMessageState

    private inline fun setEditMessageState(update: EditMessageState.() -> EditMessageState) {
        editMessageState = editMessageState.update()
    }

    fun onMessageTextChanged(text: String) {
        setEditMessageState {
            copy(
                messageText = text
            )
        }
    }

    fun onSendMessage() {
        val sendMessage = SendMessage.Builder()
            .setChatId(editMessageState.chatId)
            .setContactId(editMessageState.contactId)
            .setText(editMessageState.messageText)
            .build()

        if (sendMessage.second != null) {
            // TODO: update user on error...
        } else if (sendMessage.first != null) {
            sendMessage.first?.let { message ->
                messageRepository.sendMessage(message)
                setEditMessageState {
                    initialState()
                }
            }
        }
    }

}