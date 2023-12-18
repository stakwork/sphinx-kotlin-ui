package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.concepts.network.query.lightning.model.route.isRouteAvailable
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.*
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.feed.FeedType
import chat.sphinx.wrapper.feed.FeedUrl
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

    private val _tribeDataStateFlow: MutableStateFlow<TribeData?> by lazy {
        MutableStateFlow(null)
    }

    override val tribeDataStateFlow: StateFlow<TribeData?>
        get() = _tribeDataStateFlow.asStateFlow()

    override val chatSharedFlow: SharedFlow<Chat?> = flow {
        chatId?.let { emitAll(chatRepository.getChatByIdFlow(it)) }
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1,
    )

    init {
        scope.launch(dispatchers.mainImmediate) {
            chatRepository.getChatById(chatId)?.let { chat : Chat ->
                chatRepository.updateTribeInfo(chat)?.let { tribeData : TribeData ->

                    _tribeDataStateFlow.value = TribeData(
                        tribeData.host,
                        tribeData.chatUUID,
                        tribeData.appUrl,
                        tribeData.feedUrl,
                        tribeData.feedType,
                    )

                } ?: run {
                    _tribeDataStateFlow.value = null
                }

            } ?: run {
                _tribeDataStateFlow.value = null
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

    override fun aliasMatcher(text: String) {
        if (!text.contains("@") || text.isEmpty()) {
            resetAliasMatcher()
            return
        }

        val cursorPosition = editMessageState.messageText.value.selection.start
        val textUntilCursor = text.substring(0, cursorPosition)

        if (textUntilCursor.lastOrNull()?.toString() == " ") {
            resetAliasMatcher()
            return
        }

        textUntilCursor.split(" ").lastOrNull()?.let { lastWord ->
            if (!lastWord.contains("@") || lastWord == "@") {
                resetAliasMatcher()
                return
            }

            setAliasMatcherState {
                copy(
                    isOn = true
                )
            }

            val atPosition = text.lastIndexOf(lastWord)

            text.substring(atPosition + 1).substringBefore(" ").let {
                setAliasMatcherState {
                    copy(
                        inputText = it
                    )
                }
                generateSuggestedAliasList()
            }
        }
    }

    private fun resetAliasMatcher() {
        aliasMatcherState = AliasMatcherState(
            false,
            "",
            listOf(""),
            0
        )
    }

    private fun generateSuggestedAliasList() {
        val messageListData = MessageListState.screenState()
        if (messageListData is MessageListData.PopulatedMessageListData) {

            val inputText = aliasMatcherState.inputText.replace("\t", "").replace("\n", "")
            val aliasList = messageListData.messages.map { it.message.senderAlias?.value ?: "" }.distinct()
            val suggestedList = aliasList.filter { it.startsWith(inputText, ignoreCase = true) }.reversed()

            setAliasMatcherState {
                copy(
                    suggestedAliasList = suggestedList
                )
            }
        }
    }

    override fun onAliasNextFocus() {
        if (!aliasMatcherState.isOn) {
            return
        }

        var selectedItem = aliasMatcherState.selectedItem

        if (aliasMatcherState.selectedItem < aliasMatcherState.suggestedAliasList.lastIndex) {
            selectedItem++
        } else {
            selectedItem = 0
        }

        setAliasMatcherState {
            copy(
                selectedItem = selectedItem
            )
        }
    }

    override fun onAliasPreviousFocus() {
        if (!aliasMatcherState.isOn) {
            return
        }

        var selectedItem = aliasMatcherState.selectedItem

        if (aliasMatcherState.selectedItem > 0) {
            selectedItem--
        } else {
            selectedItem = aliasMatcherState.suggestedAliasList.lastIndex
        }

        setAliasMatcherState {
            copy(
                selectedItem = selectedItem
            )
        }
    }

    override fun onAliasSelected() {
        if (!aliasMatcherState.isOn) {
            return
        }
        val oldString = "@" + aliasMatcherState.inputText
        val newString = "@" + aliasMatcherState.suggestedAliasList[aliasMatcherState.selectedItem] + " "
        val replacedString = editMessageState.messageText.value.text.replace(oldString, newString)
        val cursorPosition = replacedString.lastIndexOf(newString) + newString.length
        editMessageState.messageText.value = TextFieldValue(replacedString, TextRange(cursorPosition))
        resetAliasMatcher()
    }

    override fun getUniqueKey(): String {
        return "TRIBE-$chatId"
    }
}