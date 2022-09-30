package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.handleSphinxTribeData
import chat.sphinx.common.state.ChatDetailData
import chat.sphinx.common.state.ChatDetailState
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.concepts.network.query.lightning.model.route.isRouteAvailable
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.message.MessageId
import chat.sphinx.wrapper.message.MessageType
import chat.sphinx.wrapper.message.isMemberApprove
import chat.sphinx.wrapper.message.isMemberReject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import theme.primary_red

class ChatTribeViewModel(
    chatId: ChatId,
    dashboardViewModel: DashboardViewModel
): ChatViewModel(
    chatId,
    dashboardViewModel
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

                chatRepository.updateTribeInfo(chat)?.let { tribeData ->
                    handleSphinxTribeData(
                        chat.name?.value ?: "Tribe",
                        tribeData
                    )
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

    override suspend fun processMemberRequest(contactId: ContactId, messageId: MessageId, type: MessageType) {
        scope.launch(dispatchers.mainImmediate) {
            val errorMessage = if (type.isMemberApprove()){
                "Failed to approve member"
            } else {
                "Failed to reject member"
            }

            if (type.isMemberApprove() || type.isMemberReject()) {
                when(messageRepository.processMemberRequest(contactId, messageId, type)) {
                    is LoadResponse.Loading -> {}
                    is Response.Success -> {}
                    is Response.Error -> {
                        toast(errorMessage, primary_red)
                    }
                }
            }
        }.join()
    }

    override suspend fun deleteTribe() {
        scope.launch(dispatchers.mainImmediate){
            getChat()?.let { chat ->
                when (chatRepository.exitAndDeleteTribe(chat)) {
                    is Response.Success -> {}
                    is Response.Error -> {
                        toast("Failed to delete tribe", primary_red)
                    }
                }
            }
            ChatDetailState.screenState(ChatDetailData.EmptyChatDetailData)
        }.join()
    }

    override var editMessageState: EditMessageState by mutableStateOf(initialState())

    override fun initialState(): EditMessageState = EditMessageState(
        chatId = chatId
    )

    override fun getUniqueKey(): String {
        return "TRIBE-$chatId"
    }
}