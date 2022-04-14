package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chat.sphinx.common.state.ChatListData
import chat.sphinx.common.state.ChatListState
import chat.sphinx.common.viewmodel.ChatUIModel

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatList() {
    val listState = rememberLazyListState()

    val chatUIModel = ChatUIModel()

    Box {
        when (val chatListData = ChatListState.screenState()) {
            is ChatListData.EmptyChatListData -> {
                Text(
                    text = "Start chatting with Sphinx"
                )
            }
            is ChatListData.PopulatedChatListData -> {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(chatListData.dashboardChats) { dashboardChat ->
                        Chat(dashboardChat)
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )
            }
        }
    }

}