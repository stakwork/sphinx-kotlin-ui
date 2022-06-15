package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.viewmodel.chat.ChatContactViewModel
import chat.sphinx.common.viewmodel.chat.ChatTribeViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId

sealed class ChatDetailData {
    object EmptyChatDetailData : ChatDetailData()

    sealed class SelectedChatDetailData(
        val dashboardChat: DashboardChat
    ): ChatDetailData() {
        abstract val chatViewModel: ChatViewModel

        class SelectedContactDetail(
            contactId: ContactId,
            dashboardChat: DashboardChat,
        ): SelectedChatDetailData(
            dashboardChat
        ) {
            override val chatViewModel: ChatViewModel = ChatContactViewModel(
                null,
                contactId
            )
        }

        class SelectedContactChatDetail(
            chatId: ChatId,
            contactId: ContactId,
            dashboardChat: DashboardChat,
        ): SelectedChatDetailData(
            dashboardChat
        ) {
            override val chatViewModel: ChatViewModel = ChatContactViewModel(
                chatId,
                contactId
            )
        }

        class SelectedTribeChatDetail(
            chatId: ChatId,
            dashboardChat: DashboardChat,
        ): SelectedChatDetailData(
            dashboardChat
        ) {
            override val chatViewModel: ChatViewModel = ChatTribeViewModel(
                chatId
            )
        }
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