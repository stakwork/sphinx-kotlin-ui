package chat.sphinx.common.viewmodel.chat


import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.contact.getColorKey
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ChatContactViewModel(
    chatId: ChatId?,
    contactId: ContactId
): ChatViewModel(
    chatId
) {
    override val chatSharedFlow: SharedFlow<Chat?> = flow {
        chatId?.let { chatId ->
            emitAll(chatRepository.getChatByIdFlow(chatId))
        } ?: repositoryDashboard.getConversationByContactIdFlow(contactId).collect { chat ->
            _chatId = chat?.id
            emit(chat)
        }
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1
    )

    private val contactSharedFlow: SharedFlow<Contact?> = flow {
        emitAll(contactRepository.getContactById(contactId))
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1,
    )

    override suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>? {
        contactSharedFlow.replayCache.firstOrNull()?.let { contact ->
            return Triple(
                contact.alias?.value?.let { ChatName(it) },
                contact.photoUrl?.value?.let { PhotoUrl(it) },
                contact.getColorKey()
            )
        } ?: contactSharedFlow.firstOrNull()?.let { contact ->
            return Triple(
                contact.alias?.value?.let { ChatName(it) },
                contact.photoUrl?.value?.let { PhotoUrl(it) },
                contact.getColorKey()
            )
        } ?: let {
            var alias: ContactAlias? = null
            var photoUrl: PhotoUrl? = null
            var colorKey: String = getRandomHexCode()

            try {
                contactSharedFlow.collect { contact ->
                    if (contact != null) {
                        alias = contact.alias
                        photoUrl = contact.photoUrl
                        colorKey = contact.getColorKey()
                        throw Exception()
                    }
                }
            } catch (e: Exception) {}
            delay(25L)

            return Triple(
                alias?.value?.let { ChatName(it) },
                photoUrl?.value?.let { PhotoUrl(it) },
                colorKey
            )
        }
    }

}