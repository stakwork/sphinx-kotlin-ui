package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
//import androidx.paging.compose.collectAsLazyPagingItems

import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.message.retrieveTextToShow


@Composable
fun MessageListUI(
//    selectedChatDetailData: ChatDetailData.SelectedChatDetailData
) {
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier.padding(
            bottom = 65.dp
        ).background( color=androidx.compose.material3.MaterialTheme.colorScheme.tertiary,)
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
                            ChatMessageUI(
                                chatMessage,
                                messageListData.chatViewModel.editMessageState,
                                messageListData.chatViewModel
                            )
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
                messageListData.chatViewModel.editMessageState.replyToMessage.value?.let { replyToMessage ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background( color=androidx.compose.material3.MaterialTheme.colorScheme.tertiary,)
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
                                        messageListData.chatViewModel.editMessageState.replyToMessage.value = null
                                    }
                                ),
                        )
                    }
                }
            }
        }
    }
}