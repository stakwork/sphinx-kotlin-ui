package chat.sphinx.common.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import chat.sphinx.common.state.ChatListData
import chat.sphinx.common.state.ChatListState
import chat.sphinx.common.store.ChatUIModel

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatList() {
    val chatUIModel = ChatUIModel()

    when (val chatListData = ChatListState.screenState()) {
        is ChatListData.EmptyChatListData -> {
            Text(
                text = "Empty List"
            )
        }
        is ChatListData.PopulatedChatListData -> {
            Text(
                text = "Messages ${chatListData.dashboardChats.size}"
            )
            LazyColumn {
                items(chatListData.dashboardChats) { dashboardChat ->
                    Text(
                        text = dashboardChat.getMessageText()
                    )
                }
            }
        }
    }
}