package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.models.viewstate.messageholder.BubbleBackground
import chat.sphinx.common.models.viewstate.messageholder.InvoiceLinesHolderViewState
import chat.sphinx.common.models.viewstate.messageholder.LayoutState
import chat.sphinx.common.models.viewstate.messageholder.MessageHolderViewState
import chat.sphinx.common.state.ChatListData
import chat.sphinx.common.state.ChatListState
import chat.sphinx.common.state.MessageListData
import chat.sphinx.common.state.MessageListState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.SphinxDispatchers
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.chat.isConversation
import chat.sphinx.wrapper.contact.*
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.getMinutesDifferenceWithDateTime
import chat.sphinx.wrapper.invite.Invite
import chat.sphinx.wrapper.message.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Sorts and filters the provided list.
 *
 * @param [dashboardChats] if `null` uses the current, already sorted list.
 * @param [filter] the type of filtering to apply to the list. See [ChatFilter].
 * */
suspend fun ArrayList<DashboardChat>.updateDashboardChats(
    lock: Mutex,
    dispatchers: SphinxDispatchers
) {
    lock.withLock {
        val sortedDashboardChats = withContext(dispatchers.default) {
            this@updateDashboardChats.sortedByDescending { it.sortBy }
        }

        this@updateDashboardChats.clear()
        this@updateDashboardChats.addAll(sortedDashboardChats)
    }
}

abstract class ChatViewModel(
    val chatId: ChatId?
) {
    var _chatId: ChatId? = chatId
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
//    val dashboardChats: ArrayList<DashboardChat> = ArrayList()
    val messageRepository = SphinxContainer.repositoryModule.messageRepository
    val repositoryDashboard = SphinxContainer.repositoryModule.repositoryDashboard
    val contactRepository = SphinxContainer.repositoryModule.contactRepository
    val chatRepository = SphinxContainer.repositoryModule.chatRepository
    val repositoryMedia = SphinxContainer.repositoryModule.repositoryMedia
    val memeServerTokenHandler = SphinxContainer.repositoryModule.memeServerTokenHandler
    val memeInputStreamHandler = SphinxContainer.networkModule.memeInputStreamHandler

    init {
        scope.launch(dispatchers.mainImmediate) {
            getChatOrNull()?.let { chat ->
                messageRepository.getAllMessagesToShowByChatId(chat.id, 20).firstOrNull()?.let { messages ->
                    MessageListState.screenState(
                        MessageListData.PopulatedMessageListData(
                            messages.map { message ->
                                ChatMessage(
                                    chat,
                                    message
                                )
                            }
                        )
                    )
                }
                delay(1000L)

                messageRepository.getAllMessagesToShowByChatId(chat.id, 1000).distinctUntilChanged().collect { messages ->
                    MessageListState.screenState(
                        MessageListData.PopulatedMessageListData(
                            messages.map { message ->
                                ChatMessage(
                                    chat,
                                    message
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    fun getRandomHexCode(): String {
        // TODO: Randomly generate a colour.
        return "#212121"
    }

    private suspend fun getMessageHolderViewStateList(messages: List<Message>) : List<MessageHolderViewState> {
        val chat = getChatOrNull()

        val chatInfo = getChatInfo()
        val chatName = chatInfo?.first
        val chatPhotoUrl = chatInfo?.second
        val chatColorKey = chatInfo?.third ?: getRandomHexCode()

        val owner = getOwner()

        val newList = ArrayList<MessageHolderViewState>(messages.size)

        chat?.let {
            withContext(dispatchers.io) {
                var groupingDate: DateTime? = null
                var openSentPaidInvoicesCount = 0
                var openReceivedPaidInvoicesCount = 0

                for ((index, message) in messages.withIndex()) {

                    val previousMessage: Message? = if (index > 0) messages[index - 1] else null
                    val nextMessage: Message? = if (index < messages.size - 1) messages[index + 1] else null

                    val groupingDateAndBubbleBackground = getBubbleBackgroundForMessage(
                        message,
                        previousMessage,
                        nextMessage,
                        groupingDate
                    )

                    groupingDate = groupingDateAndBubbleBackground.first

                    val sent = message.sender == chat.contactIds.firstOrNull()

                    if (message.type.isInvoicePayment()) {
                        if (sent) {
                            openReceivedPaidInvoicesCount -= 1
                        } else {
                            openSentPaidInvoicesCount -= 1
                        }
                    }

                    val invoiceLinesHolderViewState = InvoiceLinesHolderViewState(
                        openSentPaidInvoicesCount > 0,
                        openReceivedPaidInvoicesCount > 0
                    )

                    val isDeleted = message.status.isDeleted()

                    if (
                        (sent && !message.isPaidInvoice) ||
                        (!sent && message.isPaidInvoice)
                    ) {

                        newList.add(
                            MessageHolderViewState.Sent(
                                message,
                                chat,
                                background =  when {
                                    isDeleted -> {
                                        BubbleBackground.Gone(setSpacingEqual = false)
                                    }
                                    message.type.isInvoicePayment() -> {
                                        BubbleBackground.Gone(setSpacingEqual = false)
                                    }
                                    message.type.isGroupAction() -> {
                                        BubbleBackground.Gone(setSpacingEqual = true)
                                    }
                                    else -> {
                                        groupingDateAndBubbleBackground.second
                                    }
                                },
                                invoiceLinesHolderViewState = invoiceLinesHolderViewState,
                                messageSenderInfo = { messageCallback ->
                                    when {
                                        messageCallback.sender == chat.contactIds.firstOrNull() -> {
                                            val accountOwner = contactRepository.accountOwner.value

                                            Triple(
                                                accountOwner?.photoUrl,
                                                accountOwner?.alias,
                                                accountOwner?.getColorKey() ?: ""
                                            )
                                        }
                                        chat.type.isConversation() -> {
                                            Triple(
                                                chatPhotoUrl,
                                                chatName?.value?.toContactAlias(),
                                                chatColorKey
                                            )
                                        }
                                        else -> {
                                            Triple(
                                                messageCallback.senderPic,
                                                messageCallback.senderAlias?.value?.toContactAlias(),
                                                messageCallback.getColorKey()
                                            )
                                        }
                                    }
                                },
                                accountOwner = { owner },
                                urlLinkPreviewsEnabled = false,
                                paidTextMessageContentProvider = {
                                        messageCallback -> handlePaidTextMessageContent(messageCallback)
                                },
                                onBindDownloadMedia = {
                                    repositoryMedia.downloadMediaIfApplicable(message, sent)
                                }
                            )
                        )
                    } else {
                        newList.add(
                            MessageHolderViewState.Received(
                                message,
                                chat,
                                background = when {
                                    isDeleted -> {
                                        BubbleBackground.Gone(setSpacingEqual = false)
                                    }
                                    message.isFlagged -> {
                                        BubbleBackground.Gone(setSpacingEqual = false)
                                    }
                                    message.type.isInvoicePayment() -> {
                                        BubbleBackground.Gone(setSpacingEqual = false)
                                    }
                                    message.type.isGroupAction() -> {
                                        BubbleBackground.Gone(setSpacingEqual = true)
                                    }
                                    else -> {
                                        groupingDateAndBubbleBackground.second
                                    }
                                },
                                invoiceLinesHolderViewState = invoiceLinesHolderViewState,
                                messageSenderInfo = { messageCallback ->
                                    when {
                                        messageCallback.sender == chat.contactIds.firstOrNull() -> {
                                            val accountOwner = contactRepository.accountOwner.value

                                            Triple(
                                                accountOwner?.photoUrl,
                                                accountOwner?.alias,
                                                accountOwner?.getColorKey() ?: ""
                                            )
                                        }
                                        chat.type.isConversation() -> {
                                            Triple(
                                                chatPhotoUrl,
                                                chatName?.value?.toContactAlias(),
                                                chatColorKey
                                            )
                                        }
                                        else -> {
                                            Triple(
                                                messageCallback.senderPic,
                                                messageCallback.senderAlias?.value?.toContactAlias(),
                                                messageCallback.getColorKey()
                                            )
                                        }
                                    }
                                },
                                accountOwner = { owner },
                                urlLinkPreviewsEnabled = areUrlLinkPreviewsEnabled(),
                                paidTextMessageContentProvider = { messageCallback ->
                                    handlePaidTextMessageContent(messageCallback)
                                },
                                onBindDownloadMedia = {
                                    repositoryMedia.downloadMediaIfApplicable(message, sent)
                                }
                            )
                        )
                    }

                    if (message.isPaidInvoice) {
                        if (sent) {
                            openSentPaidInvoicesCount += 1
                        } else {
                            openReceivedPaidInvoicesCount += 1
                        }
                    }
                }
            }
        }

        return newList
    }

    private fun getBubbleBackgroundForMessage(
        message: Message,
        previousMessage: Message?,
        nextMessage: Message?,
        groupingDate: DateTime?,
    ): Pair<DateTime?, BubbleBackground> {

        val groupingMinutesLimit = 5.0
        var date = groupingDate ?: message.date

        val shouldAvoidGroupingWithPrevious = (previousMessage?.shouldAvoidGrouping() ?: true) || message.shouldAvoidGrouping()
        val isGroupedBySenderWithPrevious = previousMessage?.hasSameSenderThanMessage(message) ?: false
        val isGroupedByDateWithPrevious = message.date.getMinutesDifferenceWithDateTime(date) < groupingMinutesLimit

        val groupedWithPrevious = (!shouldAvoidGroupingWithPrevious && isGroupedBySenderWithPrevious && isGroupedByDateWithPrevious)

        date = if (groupedWithPrevious) date else message.date

        val shouldAvoidGroupingWithNext = (nextMessage?.shouldAvoidGrouping() ?: true) || message.shouldAvoidGrouping()
        val isGroupedBySenderWithNext = nextMessage?.hasSameSenderThanMessage(message) ?: false
        val isGroupedByDateWithNext = if (nextMessage != null) nextMessage.date.getMinutesDifferenceWithDateTime(date) < groupingMinutesLimit else false

        val groupedWithNext = (!shouldAvoidGroupingWithNext && isGroupedBySenderWithNext && isGroupedByDateWithNext)

        when {
            (!groupedWithPrevious && !groupedWithNext) -> {
                return Pair(date, BubbleBackground.First.Isolated)
            }
            (groupedWithPrevious && !groupedWithNext) -> {
                return Pair(date, BubbleBackground.Last)
            }
            (!groupedWithPrevious && groupedWithNext) -> {
                return Pair(date, BubbleBackground.First.Grouped)
            }
            (groupedWithPrevious && groupedWithNext) -> {
                return Pair(date, BubbleBackground.Middle)
            }
        }

        return Pair(date, BubbleBackground.First.Isolated)
    }


    private fun areUrlLinkPreviewsEnabled(): Boolean = false

    protected abstract val chatSharedFlow: SharedFlow<Chat?>

    suspend fun getChatOrNull(): Chat? {
        return chatId?.let { chatRepository.getChatById(it) }
    }

    protected suspend fun getChat(): Chat {

        chatSharedFlow.replayCache.firstOrNull()?.let { chat ->
            return chat
        }

        chatSharedFlow.firstOrNull()?.let { chat ->
            return chat
        }

        var chat: Chat? = null

        try {
            chatSharedFlow.collect {
                if (it != null) {
                    chat = it
                    throw Exception()
                }
            }
        } catch (e: Exception) {}
        delay(25L)

        return chat!!
    }

    private suspend fun handlePaidTextMessageContent(message: Message): LayoutState.Bubble.ContainerThird.Message? {
        var messageLayoutState: LayoutState.Bubble.ContainerThird.Message? = null

        scope.launch(dispatchers.mainImmediate) {
            message.retrievePaidTextAttachmentUrlAndMessageMedia()?.let { urlAndMedia ->
                urlAndMedia.second?.host?.let { host ->
                    urlAndMedia.second?.mediaKeyDecrypted?.let { mediaKeyDecrypted ->
                        memeServerTokenHandler.retrieveAuthenticationToken(host)?.let { token ->

                            val inputStream = memeInputStreamHandler.retrieveMediaInputStream(
                                urlAndMedia.first,
                                token,
                                mediaKeyDecrypted
                            )

                            var text: String? = null

                            scope.launch(dispatchers.io) {
                                text = inputStream?.bufferedReader().use { it?.readText() }
                            }.join()

                            text?.let { nnText ->
                                messageLayoutState = LayoutState.Bubble.ContainerThird.Message(text = nnText)

                                nnText.toMessageContentDecrypted()?.let { messageContentDecrypted ->
                                    messageRepository.updateMessageContentDecrypted(
                                        message.id,
                                        messageContentDecrypted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }.join()

        return messageLayoutState
    }


    private suspend fun getOwner(): Contact {
        return contactRepository.accountOwner.value.let { contact ->
            if (contact != null) {
                contact
            } else {
                var resolvedOwner: Contact? = null
                try {
                    contactRepository.accountOwner.collect { ownerContact ->
                        if (ownerContact != null) {
                            resolvedOwner = ownerContact
                            throw Exception()
                        }
                    }
                } catch (e: Exception) {
                }
                delay(25L)

                resolvedOwner!!
            }
        }
    }

    protected abstract suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>?

}