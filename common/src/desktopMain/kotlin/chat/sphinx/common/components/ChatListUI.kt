package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.state.ChatListData
import chat.sphinx.common.state.ChatListState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.dashboard.ChatListViewModel
import utils.AnimatedContainer

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatListUI() {
    val listState = rememberLazyListState()
    val chatListViewModel = remember { ChatListViewModel() }

    Box(modifier = Modifier.padding(bottom = 65.dp)) {
        when (val chatListData = ChatListState.screenState()) {
            is ChatListData.EmptyChatListData -> {}
            is ChatListData.PopulatedChatListData -> {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(chatListData.dashboardChats) { dashboardChat ->
                        ChatRow(dashboardChat)
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