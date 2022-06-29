package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
//import androidx.paging.compose.collectAsLazyPagingItems

import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import utils.getRandomColorRes


@Composable
fun MessageListUI(
//    selectedChatDetailData: ChatDetailData.SelectedChatDetailData
) {
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(
            bottom = 15.dp
        )
    ) {
        when (val messageListData = MessageListState.screenState()) {
            is MessageListData.EmptyMessageListData -> {
                SphinxSplash()
            }
            is MessageListData.PopulatedMessageListData -> {
                val listState = LazyListState()
                val chatMessages by messageListData.chatMessagesFlow.collectAsState(emptyList())
                val items = rememberSaveable{chatMessages.toMutableStateList()}
                LaunchedEffect(messageListData.chatViewModel.chatId) {
                    // If item count changes read messages...
                    messageListData.chatViewModel.readMessages()

                    // Update chat messages by loading more messages
                    delay(1000L)

                    MessageListState.screenState(
                        MessageListData.PopulatedMessageListData(
                            messageListData.chatViewModel.getChatMessages(1000L),
                            chatViewModel = messageListData.chatViewModel
                        ),
                    )
                }

                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(
                        bottom = 45.dp,
                        top = 8.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {

//                    if(items.isNotEmpty())
                    itemsIndexed(chatMessages, key = {index, item -> item.message.id }){ index, item ->
                        val currentItem= rememberSaveable{item}
                        print("index is $index with value ${item.message.messageContent?.value}")
                        ChatMessageUI(
                            currentItem,
                            messageListData.chatViewModel.editMessageState,
                            messageListData.chatViewModel, getRandomColorRes())
                    }
//                    else {
//
//                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    reverseLayout = true,
                    adapter = rememberScrollbarAdapter(scrollState = listState)
                )
                messageListData.chatViewModel.editMessageState.replyToMessage.value?.let { replyToMessage ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            modifier = Modifier.height(44.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(Color.Green) // TODO: Reply to colour
                                    .padding(16.dp)
                            )
                            // TODO: Image if available...
                            replyToMessage.message.messageMedia?.let { media ->
                                if (media.mediaType.isImage) {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = "Image",
                                        tint = Color.Green,
                                        modifier = Modifier.size(88.dp).padding(4.dp)
                                    )
                                } else {
                                    // show
                                    Icon(
                                        Icons.Default.AttachFile,
                                        contentDescription = "Attachment",
                                        tint = Color.Green,
                                        modifier = Modifier.size(88.dp).padding(4.dp)
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        end = 40.dp
                                    )
                            ) {
                                Text(
                                    replyToMessage.replyToMessageSenderAliasPreview,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    replyToMessage.replyToMessageTextPreview,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close reply to message",
                            modifier = Modifier.height(70.dp)
                                .align(Alignment.CenterEnd)
                                .width(30.dp)
                                .padding(start = 1.dp, top = 25.dp, end = 1.dp, bottom = 25.dp)
                                .clickable(
                                    onClick = {
                                        messageListData.chatViewModel.editMessageState.replyToMessage.value =
                                            null
                                    }
                                ),
                        )
                    }
                }
            }
        }
    }
}