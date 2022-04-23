package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
//import androidx.paging.compose.collectAsLazyPagingItems

import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.wrapper.message.retrieveTextToShow


@Composable
fun MessageListUI(
//    selectedChatDetailData: ChatDetailData.SelectedChatDetailData
) {
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier.padding(
            bottom = 65.dp
        )
    ) {
        when(val messageListData = MessageListState.screenState()) {
            is MessageListData.EmptyMessageListData -> {
                SphinxSplash()
            }
            is MessageListData.PopulatedMessageListData -> {
                val lazyPagingItems = messageListData.pagingData.collectAsLazyPagingItems()
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(lazyPagingItems) { chatMessage ->
                        if (chatMessage != null) {
                            ChatMessageUI(chatMessage)
                        } else {
                            ChatMessageUIPlaceholder()
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    reverseLayout = true,
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )
            }
        }
    }
}