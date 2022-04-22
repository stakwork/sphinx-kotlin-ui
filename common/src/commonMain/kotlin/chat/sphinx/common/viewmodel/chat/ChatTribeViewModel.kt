package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.MutableState
import chat.sphinx.common.state.MessageListData
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.dashboard.ChatId
import kotlinx.coroutines.flow.*

class ChatTribeViewModel(
    chatId: ChatId?
): ChatViewModel(
    chatId
) {
    override val chatSharedFlow: SharedFlow<Chat?> = flow {
        chatId?.let { emitAll(chatRepository.getChatByIdFlow(it)) }
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1,
    )

    override suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>? = null

}