package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId

sealed class ChatDetailData {
    object EmptyChatDetailData : ChatDetailData()

    sealed class SelectedChatDetailData(
        val chatId: ChatId,
        val dashboardChat: DashboardChat
    ): ChatDetailData() {
        class SelectedContactChatDetail(
            chatId: ChatId,
            val contactId: ContactId,
            dashboardChat: DashboardChat,
        ): SelectedChatDetailData(
            chatId,
            dashboardChat
        )

        class SelectedTribeChatDetail(
            chatId: ChatId,
            dashboardChat: DashboardChat,
        ): SelectedChatDetailData(
            chatId,
            dashboardChat
        )
    }
}

object ChatDetailState {
    private var screen: MutableState<ChatDetailData> = mutableStateOf(ChatDetailData.EmptyChatDetailData)

    fun screenState() : ChatDetailData {
        return screen.value
    }

    fun screenState(state: ChatDetailData) {
        screen.value = state
    }
}