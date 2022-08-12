package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.DashboardChat
import java.util.*

abstract class ChatListData {
    class EmptyChatListData : ChatListData()

    class PopulatedChatListData(
        val dashboardChats: List<DashboardChat>,
        val selectedDashboardId: String?,
    ) : ChatListData() {

        override fun hashCode(): Int {
            return dashboardChats.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true

            if (javaClass != other?.javaClass) return false

            (other as? PopulatedChatListData)?.let {
                if (
                    dashboardChats != other.dashboardChats ||
                    selectedDashboardId != other.selectedDashboardId
                ) {
                    return false
                }
            }

            return true
        }
    }
}

object ChatListState {
    private var screen: MutableState<ChatListData> = mutableStateOf(ChatListData.EmptyChatListData())

    fun screenState(): ChatListData {
        return screen.value
    }

    fun screenState(state: ChatListData) {
        if (screen.value != state) {
            screen.value = state
        }
    }
}