package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.ChatMessage

abstract class MessageListData {
    class EmptyMessageListData: MessageListData()

    class PopulatedMessageListData(
        val chatMessages: List<ChatMessage>
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