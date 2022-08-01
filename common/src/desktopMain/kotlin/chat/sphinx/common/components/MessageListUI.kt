package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.chatMesssageUI.ChatMessageUI
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.media.isImage
import theme.place_holder_text
import utils.AnimatedContainer


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
                val listState = rememberLazyListState()
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
    LazyColumn(
        state = listState,
        reverseLayout = true,
        contentPadding = PaddingValues(8.dp)
    ) {
        itemsIndexed(items, key = { index, item -> item.message.id }){ index, item ->
            print("index is $index with value ${item.message.messageContent?.value}")

            ChatMessageUI(
                item,
                chatViewModel
            )
        }
    }
}
