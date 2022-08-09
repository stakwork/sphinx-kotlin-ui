package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chat.sphinx.common.chatMesssageUI.ChatMessageUI
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import kotlinx.coroutines.launch


@Composable
fun MessageListUI(
    chatViewModel: ChatViewModel
) {
    Box {
        when (val messageListData = MessageListState.screenState()) {
            is MessageListData.EmptyMessageListData -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                )
            }
            is MessageListData.PopulatedMessageListData -> {
                val listState = remember(messageListData.chatId) { LazyListState() }

                val chatMessages = messageListData.messages
                val items= mutableStateListOf<ChatMessage>()
                items.addAll(chatMessages)

                if (chatMessages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ChatMessagesList(
                        items,
                        listState,
                        chatViewModel
                    )

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        reverseLayout = true,
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessagesList(
    items: SnapshotStateList<ChatMessage>,
    listState: LazyListState,
    chatViewModel: ChatViewModel
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        state = listState,
        reverseLayout = true,
        contentPadding = PaddingValues(8.dp)
    ) {

        chatViewModel.onNewMessageCallback = {
            scope.launch {
                if (listState.firstVisibleItemIndex <= 1) {
                    listState.scrollToItem(0)
                }
            }
        }

        itemsIndexed(items, key = { index, item -> item.message.id }){ index, item ->
            print("index is $index with value ${item.message.messageContent?.value}")

            ChatMessageUI(
                item,
                chatViewModel
            )
        }
    }
}
