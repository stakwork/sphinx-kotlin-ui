package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatListUI() {
    val listState = rememberLazyListState()
    val chatListViewModel = remember { ChatListViewModel() }

    Box {
        when (val chatListData = ChatListState.screenState()) {
            is ChatListData.EmptyChatListData -> {
                SphinxSplash()
            }
            is ChatListData.PopulatedChatListData -> {
//                val counters = remember { chatListData.dashboardChats.toMutableStateList() }
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(4.dp)
                ) {
//                    itemsIndexed(chatListData.dashboardChats){index,chat->
//                        ChatRow(counters.get(index))
//                    }
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