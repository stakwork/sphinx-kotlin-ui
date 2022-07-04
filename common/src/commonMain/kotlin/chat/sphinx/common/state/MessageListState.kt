package chat.sphinx.common.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingData
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import kotlinx.coroutines.flow.Flow

abstract class MessageListData {
    object EmptyMessageListData: MessageListData()

    class PopulatedMessageListData(
        val chatViewModel: ChatViewModel
    ): MessageListData()
}

object MessageListState {
    private val screen: MutableState<MessageListData> = mutableStateOf(MessageListData.EmptyMessageListData)

    fun screenState() : MessageListData {
        return screen.value
    }

    fun screenState(state: MessageListData) {
        screen.value = state
    }
}