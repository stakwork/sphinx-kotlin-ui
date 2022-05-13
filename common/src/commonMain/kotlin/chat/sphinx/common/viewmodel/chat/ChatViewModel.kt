package chat.sphinx.common.viewmodel.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import androidx.paging.map
import chat.sphinx.common.components.landing.SphinxDialog
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.*
import chat.sphinx.concepts.repository.message.model.SendMessage
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.Response
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.message.MessageUUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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

    val replyToMessage: MutableState<ChatMessage?> = mutableStateOf(null)

    init {
        scope.launch(dispatchers.mainImmediate) {
            val owner = getOwner()
            getChatOrNull()?.let { chat ->
                MessageListState.screenState(
                    MessageListData.PopulatedMessageListData(
                        messageRepository.getAllMessagesToShowByChatIdPaginated(chat.id)
                            .map { pagingData: PagingData<Message> ->
                                pagingData.map { message: Message ->
                                    ChatMessage(
                                        chat,
                                        message,
                                        accountOwner = { owner },
                                        boostMessage = {
                                            boostMessage(chat, message.uuid)
                                        },
                                        flagMessage = {
                                            // TODO: Requires confirmation
                                            flagMessage(chat, message)
                                        },
                                        deleteMessage = {
                                            // TODO: Requires confirmation...
                                            deleteMessage(message)
                                        },
                                        replyToMessage
                                    )
                                }
                            },
                        replyToMessage
                    ),
                )
            }
        }
    }

    private fun boostMessage(chat: Chat, messageUUID: MessageUUID?) {
        if (messageUUID == null) return

        scope.launch(dispatchers.mainImmediate) {
            val response = messageRepository.boostMessage(
                chat.id,
                chat.pricePerMessage ?: Sat(0),
                chat.escrowAmount ?: Sat(0),
                messageUUID,
            )

            when (response) {
                is Response.Error -> {
                    // TODO: submitSideEffect(ChatSideEffect.Notify(app.getString(R.string.notify_boost_failure)))
                }
                is Response.Success -> {}
            }
        }
    }

    fun flagMessage(chat: Chat, message: Message) {
        scope.launch(dispatchers.mainImmediate) {
            messageRepository.flagMessage(message, chat)
        }
    }

    fun deleteMessage(message: Message) {
        scope.launch(dispatchers.mainImmediate) {
            when (messageRepository.deleteMessage(message)) {
                is Response.Error -> {
                    // TODO: submitSideEffect(ChatSideEffect.Notify("Failed to delete Message"))
                }
                is Response.Success -> {}
            }
        }
    }

    fun getRandomHexCode(): String {
        // TODO: Randomly generate a colour.
        return "#212121"
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