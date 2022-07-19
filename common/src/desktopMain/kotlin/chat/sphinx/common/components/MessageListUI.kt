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
import com.example.compose.place_holder_text
import utils.AnimatedContainer


@Composable
fun MessageListUI(
    chatViewModel: ChatViewModel
) {
    Box(
        modifier = Modifier.padding(
            bottom = 15.dp
        )
    ) {
        when (val messageListData = MessageListState.screenState()) {
            is MessageListData.EmptyMessageListData -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                )
            }
            is MessageListData.PopulatedMessageListData -> {
                val listState = LazyListState()
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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 45.dp)
                    ) {
                        chatViewModel.editMessageState.attachmentInfo.value?.let { attachmentInfo ->
                            Text(
                                attachmentInfo.filePath.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            )
                        }
                        chatViewModel.editMessageState.replyToMessage.value?.let { replyToMessage ->
                            AnimatedContainer(
                                fromTopToBottom = 20, modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
                            ) {
                                Box(

                                ) {
                                    Row(
                                        modifier = Modifier.height(44.dp)
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .fillMaxHeight()
                                                .background(Color.Green) // TODO: Reply to colour
                                                .padding(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
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
                                                .fillMaxWidth(0.9f)
                                                .padding(
                                                    end = 40.dp
                                                )
                                        ) {
                                            Text(
                                                replyToMessage.replyToMessageSenderAliasPreview,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                            Text(
                                                replyToMessage.replyToMessageTextPreview,
                                                overflow = TextOverflow.Ellipsis,
                                                color = place_holder_text, fontSize = 11.sp
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.fillMaxSize().align(
                                                Alignment.Bottom
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                contentDescription = "Close reply to message",
                                                modifier = Modifier.size(20.dp)
                                                    .align(Alignment.BottomCenter)
//                                        .width(30.dp)
                                                    .clickable(
                                                        onClick = {
                                                            chatViewModel.editMessageState.replyToMessage.value =
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
        contentPadding = PaddingValues(
            bottom = if (chatViewModel?.isReplying()) 94.dp else 50.dp,
            top = 8.dp,
            start = 8.dp,
            end = 8.dp
        )
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
