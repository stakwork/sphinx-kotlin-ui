package chat.sphinx.common.viewmodel.chat

import androidx.compose.ui.graphics.Color
import androidx.paging.PagingData
import androidx.paging.map
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.*
import chat.sphinx.concepts.meme_input_stream.MemeInputStreamHandler
import chat.sphinx.concepts.meme_server.MemeServerTokenHandler
import chat.sphinx.concepts.repository.message.model.SendMessage
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.Response
import chat.sphinx.utils.UserColorsHelper
import chat.sphinx.utils.createAttachmentFileDownload
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.getColorKey
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.MessageMedia
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import utils.getRandomColorRes
import java.io.InputStream

suspend inline fun MessageMedia.retrieveRemoteMediaInputStream(
    url: String,
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
): InputStream? {
    return localFile?.toFile()?.inputStream() ?: host?.let { mediaHost ->
        memeServerTokenHandler.retrieveAuthenticationToken(mediaHost)?.let { authenticationToken ->
            memeInputStreamHandler.retrieveMediaInputStream(
                url,
                authenticationToken,
                mediaKeyDecrypted
            )
        }
    }
}

abstract class ChatViewModel(
    val chatId: ChatId?
) {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val messageRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).messageRepository
    val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard
    val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    val chatRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).chatRepository
    val repositoryMedia = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryMedia
    val memeServerTokenHandler = SphinxContainer.repositoryModule(sphinxNotificationManager).memeServerTokenHandler
    val memeInputStreamHandler = SphinxContainer.networkModule.memeInputStreamHandler
    private val attachmentFileDownloader: chat.sphinx.utils.AttachmentFileDownloader = createAttachmentFileDownload(
        memeServerTokenHandler,
        memeInputStreamHandler
    )

    private val colorsHelper = UserColorsHelper(SphinxContainer.appModule.dispatchers)

    init {
        MessageListState.screenState(
            MessageListData.PopulatedMessageListData(this)
        )
    }

    private suspend fun loadChatMessages() {
        getChat()?.let { chat ->
            messageRepository.getAllMessagesToShowByChatId(chat.id, 50).firstOrNull()?.let { messages ->
                processChatMessages(chat, messages)
            }

            delay(1000L)

            messageRepository.getAllMessagesToShowByChatId(chat.id, 1000).distinctUntilChanged().collect { messages ->
                processChatMessages(chat, messages)
            }
        } ?: run {
            MessageListState.screenState(
                MessageListData.EmptyMessageListData
            )
        }
    }

    fun getChatMessageFlow(): Flow<List<ChatMessage>> = flow {
        getChat()?.let { chat ->
            messageRepository.getAllMessagesToShowByChatId(chat.id, 50).firstOrNull()?.let { messages ->
                emit(processChatMessages(chat, messages))
            }

            delay(1000L)

            messageRepository.getAllMessagesToShowByChatId(chat.id, 1000).distinctUntilChanged().collect { messages ->
                emit(processChatMessages(chat, messages))
            }
        }
    }

    private suspend fun processChatMessages(chat: Chat, messages: List<Message>): List<ChatMessage> {
        val owner = getOwner()
        val contact = getContact()

        val chatMessages = messages.reversed().map { message ->

            val colorKey = contact?.getColorKey() ?: message.getColorKey()
            val colorInt = colorsHelper.getColorIntForKey(
                colorKey,
                Integer.toHexString(getRandomColorRes().hashCode())
            )

            ChatMessage(
                chat,
                contact,
                message,
                Color(colorInt),
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
                }
            )
        }
        return chatMessages
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

    private fun readMessages() {
        chatId?.let {
            messageRepository.readMessages(chatId)
        }
    }

    fun getRandomHexCode(): String {
        // TODO: Randomly generate a colour.
        return "#212121"
    }

    suspend fun getOwner(): Contact {
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

    private suspend fun getChat(): Chat? {
        return chatId?.let { chatRepository.getChatById(it) }
    }

    abstract suspend fun getContact(): Contact?

    protected abstract suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>?

    // Message sending logic...
    abstract var editMessageState: EditMessageState
        protected set

    abstract fun initialState(): EditMessageState

    private inline fun setEditMessageState(update: EditMessageState.() -> EditMessageState) {
        editMessageState = editMessageState.update()
    }

    fun onMessageTextChanged(text: String) {
        editMessageState.messageText.value = text
    }

    fun onSendMessage() {
        val sendMessage = SendMessage.Builder()
            .setChatId(editMessageState.chatId)
            .setContactId(editMessageState.contactId)
            .setText(editMessageState.messageText.value)
            .also { builder ->
                editMessageState.replyToMessage.value?.message?.uuid?.value?.toReplyUUID().let { replyUUID ->
                    builder.setReplyUUID(replyUUID)
                }
            }
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

    fun saveFile(message: Message) {
        attachmentFileDownloader.saveFile(message)
    }
}