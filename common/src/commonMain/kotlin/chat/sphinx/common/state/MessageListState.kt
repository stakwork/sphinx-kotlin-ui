package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import chat.sphinx.common.models.ChatMessage
import kotlinx.coroutines.flow.Flow

abstract class MessageListData {
    class EmptyMessageListData: MessageListData()

    class PopulatedMessageListData(
        val pagingData: Flow<PagingData<ChatMessage>>,
        val replyToMessage: MutableState<ChatMessage?>
    ): MessageListData()
}

object MessageListState {
    private val screen: MutableState<MessageListData> = mutableStateOf(MessageListData.EmptyMessageListData())

    fun screenState() : MessageListData {
        return screen.value
    }

    fun screenState(state: MessageListData) {
        screen.value = state
    }
}