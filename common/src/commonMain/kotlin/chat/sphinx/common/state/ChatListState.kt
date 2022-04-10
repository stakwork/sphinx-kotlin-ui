package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.DashboardChat

abstract class ChatListData {
    class EmptyChatListData: ChatListData()

    class PopulatedChatListData(
        val dashboardChats: List<DashboardChat>
    ): ChatListData()
}

object ChatListState {
    private var screen: MutableState<ChatListData> = mutableStateOf(ChatListData.EmptyChatListData())

    fun screenState() : ChatListData {
        return screen.value
    }

    fun screenState(state: ChatListData) {
        screen.value = state
    }
}