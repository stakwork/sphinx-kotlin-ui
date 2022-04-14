package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.DashboardChat

abstract class ChatDetailData {
    class EmptyChatDetailData: ChatDetailData()

    class SelectedChatDetail(
        val dashboardChat: DashboardChat
    ): ChatDetailData()
}

object ChatDetailState {
    private var screen: MutableState<ChatDetailData> = mutableStateOf(ChatDetailData.EmptyChatDetailData())

    fun screenState() : ChatDetailData {
        return screen.value
    }

    fun screenState(state: ChatDetailData) {
        screen.value = state
    }
}