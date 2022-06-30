package chat.sphinx.common.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.media.isImage
import utils.getRandomColorRes


@Composable
fun MessageListUI(
    chatViewModel: ChatViewModel
) {
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.background).padding(
            bottom = 15.dp
        )
    ) {
        when (val messageListData = MessageListState.screenState()) {
            is MessageListData.EmptyMessageListData -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
                )
            }
            is MessageListData.PopulatedMessageListData -> {
                val listState = LazyListState()
                val chatMessages = messageListData.messages
//                val items = rememberSaveable{ chatMessages.toMutableStateList() }

                if (chatMessages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
                    )
                } else {
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
                        itemsIndexed(chatMessages, key = { index, item -> item.message.id }){ index, item ->
                            val currentItem = rememberSaveable{ item }
                            print("index is $index with value ${item.message.messageContent?.value}")
                            ChatMessageUI(
                                currentItem,
                                chatViewModel.editMessageState,
                                chatViewModel,
                                getRandomColorRes())
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        reverseLayout = true,
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                    chatViewModel.editMessageState.replyToMessage.value?.let { replyToMessage ->
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
                                            chatViewModel.editMessageState.replyToMessage.value = null
                                        }
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}