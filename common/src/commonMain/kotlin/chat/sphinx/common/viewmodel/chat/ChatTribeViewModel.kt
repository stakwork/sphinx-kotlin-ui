package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.concepts.network.query.lightning.model.route.isRouteAvailable
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatTribeViewModel(
    chatId: ChatId
): ChatViewModel(
    chatId
) {
    override val chatSharedFlow: SharedFlow<Chat?> = flow {
        chatId?.let { emitAll(chatRepository.getChatByIdFlow(it)) }
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1,
    )

    init {
        scope.launch(dispatchers.mainImmediate) {
            chatRepository.getChatById(chatId)?.let { chat ->

//                moreOptionsMenuStateFlow.value = if (chat.isTribeOwnedByAccount(getOwner().nodePubKey)) {
//                    MoreMenuOptionsViewState.OwnTribe
//                } else {
//                    MoreMenuOptionsViewState.NotOwnTribe
//                }

                chatRepository.updateTribeInfo(chat)?.let { _ ->

//                    _feedDataStateFlow.value = TribeFeedData.Result.FeedData(
//                        tribeData.host,
//                        tribeData.feedUrl,
//                        tribeData.chatUUID,
//                        tribeData.feedType,
//                        chat.metaData,
//                    )

                } ?: run {
//                    _feedDataStateFlow.value = TribeFeedData.Result.NoFeed
                }

            } ?: run {
//                _feedDataStateFlow.value = TribeFeedData.Result.NoFeed
            }
        }
    }

    override suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>? = null

    override suspend fun getContact(): Contact? {
        return null
    }

    override val checkRoute: Flow<LoadResponse<Boolean, ResponseError>> = flow {
        networkQueryLightning.checkRoute(chatId).collect { response ->
            when (response) {
                is LoadResponse.Loading -> {
                    emit(response)
                }
                is Response.Error -> {
                    emit(response)
                }
                is Response.Success -> {
                    emit(Response.Success(response.value.isRouteAvailable))
                }
            }
        }
    }

    override var editMessageState: EditMessageState by mutableStateOf(initialState())

    override fun initialState(): EditMessageState = EditMessageState(
        chatId = chatId
    )
}