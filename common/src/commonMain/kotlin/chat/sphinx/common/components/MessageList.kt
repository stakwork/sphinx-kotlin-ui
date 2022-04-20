package chat.sphinx.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.models.viewstate.messageholder.MessageHolderViewState
import chat.sphinx.common.state.ChatDetailData
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.common.viewmodel.chat.ChatContactViewModel
import chat.sphinx.common.viewmodel.chat.ChatTribeViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
fun MessageList(
    selectedChatDetailData: ChatDetailData.SelectedChatDetailData
) {
    val listState = rememberLazyListState()
    val chatViewModel: ChatViewModel = remember {
        when (selectedChatDetailData) {
            is ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail -> ChatContactViewModel(
                selectedChatDetailData.chatId,
                selectedChatDetailData.contactId
            )
            is ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail -> ChatTribeViewModel(
                selectedChatDetailData.chatId
            )
        }
    }


    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        when(val messageListData = MessageListState.screenState()) {
            is MessageListData.EmptyMessageListData -> {
                SphinxSplash()
            }
            is MessageListData.PopulatedMessageListData -> {
                Text(
                    text = messageListData.messageHolderViewStates.size.toString()
                )
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(messageListData.messageHolderViewStates.reversed()) { messageHolderViewState: MessageHolderViewState ->
                        Text(
                            text = messageHolderViewState.message.retrieveTextToShow() ?: "failed to show message"
                        )
                    }
                }
            }
        }
    }


}