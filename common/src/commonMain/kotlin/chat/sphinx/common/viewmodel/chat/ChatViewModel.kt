package chat.sphinx.common.viewmodel.chat

import androidx.annotation.ColorInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import chat.sphinx.common.components.AudioPlayer
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.concepts.link_preview.model.TribePreviewName
import chat.sphinx.concepts.link_preview.model.toPreviewImageUrlOrNull
import chat.sphinx.concepts.meme_input_stream.MemeInputStreamHandler
import chat.sphinx.concepts.meme_server.MemeServerTokenHandler
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.concepts.repository.message.model.SendMessage
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.UserColorsHelper
import chat.sphinx.utils.linkify.LinkSpec
import chat.sphinx.utils.linkify.LinkTag
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.*
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.getColorKey
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.getMinutesDifferenceWithDateTime
import chat.sphinx.wrapper.isDifferentDayThan
import chat.sphinx.wrapper.lightning.*
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.MessageMedia
import chat.sphinx.wrapper.message.media.toFileName
import chat.sphinx.wrapper.tribe.TribeJoinLink
import chat.sphinx.wrapper.tribe.toTribeJoinLink
import chat.sphinx.wrapper_chat.NotificationLevel
import chat.sphinx.wrapper_chat.isMuteChat
import com.soywiz.korio.util.substringAfterLastOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.Path
import theme.primary_green
import theme.primary_red
import utils.deduceMediaType
import utils.getRandomColorRes
import java.io.IOException
import java.io.InputStream

suspend inline fun MessageMedia.retrieveRemoteMediaInputStream(
    url: String,
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
): InputStream? {
    return localFile?.toFile()?.inputStream() ?: host?.let { mediaHost ->
        memeServerTokenHandler.retrieveAuthenticationToken(mediaHost)?.let { authenticationToken ->
            memeInputStreamHandler.retrieveMediaInputStream(
                url,
                authenticationToken,
                mediaKeyDecrypted
            )?.first
        }
    }
}


abstract class ChatViewModel(
    val chatId: ChatId?,
    val dashboardViewModel: DashboardViewModel
) {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    val messageRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).messageRepository
    val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard
    val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    val chatRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).chatRepository
    private val repositoryMedia = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryMedia
    val memeServerTokenHandler = SphinxContainer.repositoryModule(sphinxNotificationManager).memeServerTokenHandler
    val memeInputStreamHandler = SphinxContainer.networkModule.memeInputStreamHandler
    private val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler
    private val linkPreviewHandler = SphinxContainer.networkModule.linkPreviewHandler

    val networkQueryLightning = SphinxContainer.networkModule.networkQueryLightning
    val networkQueryPeople = SphinxContainer.networkModule.networkQuerySaveProfile

    private val colorsHelper = UserColorsHelper(SphinxContainer.appModule.dispatchers)
    private var messagesLoadJob: Job? = null

    var onNewMessageCallback: (() -> Unit)? = null
    private var messagesSize: Int = 0

//    fun playAudio(){
//        scope.launch(dispatchers.mainImmediate) {
//            resourcesVfs["sound/parte.mp3"].readSound()
//        }
//
//    }

    val audioPlayer = AudioPlayer()

    enum class ChatActionsMode {
        MENU, REQUEST, SEND_AMOUNT, SEND_TEMPLATE, SEND_TRIBE, TRIBE_PROFILE
    }

    private val _chatActionsStateFlow: MutableStateFlow<Pair<ChatActionsMode, PaymentViewModel.PaymentData?>?> by lazy {
        MutableStateFlow(null)
    }

    val chatActionsStateFlow: StateFlow<Pair<ChatActionsMode, PaymentViewModel.PaymentData?>?>
        get() = _chatActionsStateFlow.asStateFlow()

    fun setChatActionsStateFlow(data: Pair<ChatActionsMode, PaymentViewModel.PaymentData?>) {
        _chatActionsStateFlow.value = data
    }

    fun toggleChatActionsPopup(
        mode: ChatActionsMode,
        data: PaymentViewModel.PaymentData? = null
    ) {
        if (mode == ChatActionsMode.REQUEST) {
            toast("Request amount not implemented yet")
            return
        }

        _chatActionsStateFlow.value = Pair(mode, data)
    }

    fun hideChatActionsPopup() {
        _chatActionsStateFlow.value = null
    }

    var tribeProfileState: TribeProfileState by mutableStateOf(initialTribeProfileState())

    private fun initialTribeProfileState(): TribeProfileState = TribeProfileState()

    private inline fun setTribeProfileState(update: TribeProfileState.() -> TribeProfileState) {
        tribeProfileState = tribeProfileState.update()
    }

    fun loadPersonData(person: MessagePerson) {
        scope.launch(dispatchers.mainImmediate) {
            networkQueryPeople.getTribeMemberProfile(person).collect { loadResponse ->
                when (loadResponse) {
                    is LoadResponse.Loading -> {
                        setTribeProfileState {
                            copy(
                                loadingTribeProfile = true
                            )
                        }
                    }
                    is Response.Error -> {
                        _chatActionsStateFlow.value =
                            Pair(ChatActionsMode.SEND_TRIBE, chatActionsStateFlow.value?.second)
                    }
                    is Response.Success -> {
                        setTribeProfileState {
                            copy(
                                name = loadResponse.value.owner_alias,
                                description = loadResponse.value.description,
                                profilePicture = loadResponse.value.img,
                                codingLanguages = loadResponse.value.extras?.codingLanguages ?: "-",
                                priceToMeet = loadResponse.value.price_to_meet.toString(),
                                posts = if (loadResponse.value.extras?.post?.size != null) {
                                    loadResponse.value.extras?.post?.size.toString()
                                } else {
                                    "0"
                                },
                                twitter = loadResponse.value.extras?.twitter?.first()?.formattedValue.toString(),
                                github = loadResponse.value.extras?.github?.first()?.formattedValue.toString(),
                                loadingTribeProfile = false
                            )
                        }
                    }
                }
            }
        }
    }

    private val _notificationLevelStateFlow: MutableStateFlow<Pair<Boolean, NotificationLevel?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val notificationLevelStateFlow: StateFlow<Pair<Boolean, NotificationLevel?>>
        get() = _notificationLevelStateFlow.asStateFlow()

    init {
        messagesLoadJob = scope.launch(dispatchers.mainImmediate) {
            loadChatMessages()
        }

        scope.launch(dispatchers.io) {
            readMessages()
        }
    }

    private var screenInit: Boolean = false
    fun screenInit() {
        if (screenInit) {
            return
        } else {
            screenInit = true
        }

        scope.launch(dispatchers.mainImmediate) {
            checkChatStatus()
        }
    }

    fun cancelMessagesJob() {
        messagesLoadJob?.cancel()
    }

    private suspend fun loadChatMessages() {
        getChat()?.let { chat ->
            messageRepository.getAllMessagesToShowByChatId(chat.id, 50).firstOrNull()?.let { messages ->
                processChatMessages(chat, messages)
            }

            delay(500L)

            messageRepository.getAllMessagesToShowByChatId(chat.id, 1000).distinctUntilChanged().collect { messages ->
                processChatMessages(chat, messages)
            }
        } ?: run {
            MessageListState.screenState(
                MessageListData.EmptyMessageListData
            )
        }
    }

    private suspend fun checkChatStatus() {
        getChat()?.let { chat ->
            if (chat.isPrivateTribe() && chat.status.isPending()) {
                toast("Waiting for admin approval", delay = 3000L)
            }
        }
    }

    private suspend fun processChatMessages(chat: Chat, messages: List<Message>) {
        val owner = getOwner()
        val contact = getContact()

        val tribeAdmin = if (chat.ownerPubKey != null) {
            contactRepository.getContactByPubKey(chat.ownerPubKey!!).firstOrNull()
        } else {
            null
        }

        var contactColorInt: Int? = null

        contact?.let { nnContact ->
            val contactColorKey = nnContact.getColorKey()
            contactColorInt = colorsHelper.getColorIntForKey(
                contactColorKey,
                Integer.toHexString(getRandomColorRes().hashCode())
            )
        }

        val chatMessages: MutableList<ChatMessage> = mutableListOf()
        var groupingDate: DateTime? = null

        messages.withIndex().forEach { (index, message) ->

            val colors = getColorsMapFor(message, contactColorInt, tribeAdmin)

            val previousMessage: Message? = if (index > 0) messages[index - 1] else null
            val nextMessage: Message? = if (index < messages.size - 1) messages[index + 1] else null

            val groupingDateAndBubbleBackground = getBubbleBackgroundForMessage(
                message,
                previousMessage,
                nextMessage,
                groupingDate
            )

            groupingDate = groupingDateAndBubbleBackground.first

            if (
                previousMessage == null ||
                message.date.isDifferentDayThan(previousMessage.date)
            ) {
                chatMessages.add(
                    ChatMessage(
                        chat,
                        contact,
                        message,
                        colors,
                        isSeparator = true,
                        accountOwner = { owner },
                        boostMessage = {},
                        flagMessage = {},
                        deleteMessage = {},
                        previewProvider = { handleLinkPreview(it) },
                        background = BubbleBackground.Gone
                    )
                )
            }

            chatMessages.add(
                ChatMessage(
                    chat,
                    contact,
                    message,
                    colors,
                    accountOwner = { owner },
                    boostMessage = {
                        boostMessage(chat, message.uuid)
                    },
                    flagMessage = {
                        confirm(
                            "Confirm Flagging message",
                            "Are you sure you want to flag this message? This action can not be undone"
                        ) {
                            flagMessage(chat, message)
                        }
                    },
                    deleteMessage = {
                        confirm(
                            "Confirm Deleting message",
                            "Are you sure you want to delete this message? This action can not be undone"
                        ) {
                            deleteMessage(message)
                        }
                    },
                    previewProvider = { handleLinkPreview(it) },
                    background = groupingDateAndBubbleBackground.second
                )
            )
        }

        MessageListState.screenState(
            MessageListData.PopulatedMessageListData(
                chat.id,
                chatMessages.reversed()
            )
        )

        if (messagesSize != messages.size) {
            messagesSize = messages.size

            delay(200L)
            onNewMessageCallback?.invoke()
        }
    }

    private fun getBubbleBackgroundForMessage(
        message: Message,
        previousMessage: Message?,
        nextMessage: Message?,
        groupingDate: DateTime?,
    ): Pair<DateTime?, BubbleBackground> {

        val groupingMinutesLimit = 5.0
        var date = groupingDate ?: message.date

        val shouldAvoidGroupingWithPrevious =
            (previousMessage?.shouldAvoidGrouping() ?: true) || message.shouldAvoidGrouping()
        val isGroupedBySenderWithPrevious = previousMessage?.hasSameSenderThanMessage(message) ?: false
        val isGroupedByDateWithPrevious = message.date.getMinutesDifferenceWithDateTime(date) < groupingMinutesLimit

        val groupedWithPrevious =
            (!shouldAvoidGroupingWithPrevious && isGroupedBySenderWithPrevious && isGroupedByDateWithPrevious)

        date = if (groupedWithPrevious) date else message.date

        val shouldAvoidGroupingWithNext = (nextMessage?.shouldAvoidGrouping() ?: true) || message.shouldAvoidGrouping()
        val isGroupedBySenderWithNext = nextMessage?.hasSameSenderThanMessage(message) ?: false
        val isGroupedByDateWithNext =
            if (nextMessage != null) nextMessage.date.getMinutesDifferenceWithDateTime(date) < groupingMinutesLimit else false

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

    open suspend fun processMemberRequest(
        contactId: ContactId,
        messageId: MessageId,
        type: MessageType,
    ) {
    }

    open suspend fun deleteTribe() {}

    private suspend fun getColorsMapFor(
        message: Message,
        contactColor: Int?,
        tribeAdmin: Contact?
    ): Map<Long, Int> {
        var colors: MutableMap<Long, Int> = mutableMapOf()

        contactColor?.let {
            colors[message.id.value] = contactColor
        } ?: run {
            val colorKey = message.getColorKey()
            val colorInt = colorsHelper.getColorIntForKey(
                colorKey,
                Integer.toHexString(getRandomColorRes().hashCode())
            )

            colors[message.id.value] = colorInt

            if (message.type.isDirectPayment() && tribeAdmin != null) {
                val recipientColorKey = message.getRecipientColorKey(tribeAdmin.id)
                val recipientColorInt = colorsHelper.getColorIntForKey(
                    recipientColorKey,
                    Integer.toHexString(getRandomColorRes().hashCode())
                )

                colors[-message.id.value] = recipientColorInt
            }
        }

        for (m in message.reactions ?: listOf()) {
            contactColor?.let {
                colors[m.id.value] = contactColor
            } ?: run {
                val colorKey = m.getColorKey()
                val colorInt = colorsHelper.getColorIntForKey(
                    colorKey,
                    Integer.toHexString(getRandomColorRes().hashCode())
                )

                colors[m.id.value] = colorInt
            }
        }

        message.replyMessage?.let { replyMessage ->
            contactColor?.let {
                colors[replyMessage.id.value] = contactColor
            } ?: run {
                val colorKey = replyMessage.getColorKey()
                val colorInt = colorsHelper.getColorIntForKey(
                    colorKey,
                    Integer.toHexString(getRandomColorRes().hashCode())
                )

                colors[replyMessage.id.value] = colorInt
            }
        }

        return colors
    }

    private fun boostMessage(chat: Chat, messageUUID: MessageUUID?) {
        if (messageUUID == null) return

        scope.launch(dispatchers.mainImmediate) {
            val response = messageRepository.boostMessage(
                chat.id,
                chat.pricePerMessage ?: Sat(0),
                chat.escrowAmount ?: Sat(0),
                messageUUID,
            )

            when (response) {
                is Response.Error -> {
                    toast("Boost payment failed", primary_red)
                }
                is Response.Success -> {}
            }
        }
    }

    private fun flagMessage(chat: Chat, message: Message) {
        scope.launch(dispatchers.mainImmediate) {
            messageRepository.flagMessage(message, chat)
        }
    }

    private fun deleteMessage(message: Message) {
        scope.launch(dispatchers.mainImmediate) {
            when (messageRepository.deleteMessage(message)) {
                is Response.Error -> {
                    toast("Failed to delete Message", primary_red)
                }
                is Response.Success -> {}
            }
        }
    }

    fun readMessages() {
        chatId?.let {
            messageRepository.readMessages(chatId)
        }
    }

    fun getRandomHexCode(): String {
        // TODO: Randomly generate a colour.
        return "#212121"
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

    abstract val chatSharedFlow: SharedFlow<Chat?>

    suspend fun getChat(): Chat? {
        return chatId?.let { chatRepository.getChatById(it) }
    }

    abstract suspend fun getContact(): Contact?

    protected abstract suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>?

    abstract val checkRoute: Flow<LoadResponse<Boolean, ResponseError>>

    // Message sending logic...
    abstract var editMessageState: EditMessageState
        protected set

    abstract fun initialState(): EditMessageState

    abstract fun getUniqueKey(): String

    private inline fun setEditMessageState(update: EditMessageState.() -> EditMessageState) {
        editMessageState = editMessageState.update()
    }

    fun onMessageTextChanged(text: String) {
        editMessageState.messageText.value = TextFieldValue(text)
    }

    fun onTribeMessageTextChanged(text: String) {
        editMessageState.messageText.value = TextFieldValue(text)
        aliasMatcher(text)
    }

    var aliasMatcherState: AliasMatcherState by mutableStateOf(initialAliasMatcherState())

    private fun initialAliasMatcherState(): AliasMatcherState = AliasMatcherState()

    fun onAliasNextFocus() {
        if (aliasMatcherState.selectedItem.value < aliasMatcherState.suggestedAliasList.value.lastIndex) {
            aliasMatcherState.selectedItem.value++
        }
        else {
            aliasMatcherState.selectedItem.value = 0
        }
    }

    fun onAliasPreviousFocus() {
        if (aliasMatcherState.selectedItem.value > 0) {
            aliasMatcherState.selectedItem.value--
        }
        else {
            aliasMatcherState.selectedItem.value = aliasMatcherState.suggestedAliasList.value.lastIndex
        }
    }

    private fun aliasMatcher(text: String) {
        if (text.isNotEmpty()) {
            if (text.takeLast(2).contains(regex = Regex("@."))) {
                aliasMatcherState.isOn.value = true
            }
            if (aliasMatcherState.isOn.value) {
                text.substringAfterLastOrNull('@')?.let {
                    aliasMatcherState.inputText.value = it
                    generateSuggestedAliasList()
                } ?: run {
                    resetAliasMatcher()
                }
            }
            if (aliasMatcherState.inputText.value.contains(' ') ||
                    aliasMatcherState.inputText.value.length > 4)
            {
                resetAliasMatcher()
            }
        }
        else {
            resetAliasMatcher()
        }
    }

    fun resetAliasMatcher() {
        aliasMatcherState.isOn.value = false
        aliasMatcherState.inputText.value = ""
        aliasMatcherState.selectedItem.value = 0
    }

    private fun generateSuggestedAliasList() {
        val messageListData = MessageListState.screenState()
        if (messageListData is MessageListData.PopulatedMessageListData) {
            val inputText = aliasMatcherState.inputText.value.replace("\t", "").replace("\n", "")
            val aliasList = messageListData.messages.map { it.message.senderAlias?.value ?: "" }.distinct()
            val suggestedList = aliasList.filter { it.startsWith(inputText, ignoreCase = true) }.reversed()

            if (suggestedList.size > 3) {
                aliasMatcherState.suggestedAliasList.value = suggestedList.slice(0..2)
            }
            else {
                aliasMatcherState.suggestedAliasList.value = suggestedList
            }
        }
    }

    fun onSelectAlias() {
        val oldString = "@" + aliasMatcherState.inputText.value
        val newString = "@" + aliasMatcherState.suggestedAliasList.value.get(aliasMatcherState.selectedItem.value)
        val replacedString = editMessageState.messageText.value.text.replace(oldString, newString)
        editMessageState.messageText.value = TextFieldValue(replacedString, TextRange(0))
    }

    fun onPriceTextChanged(text: String) {
        try {
            editMessageState.price.value = text.toLong()
        } catch (e: NumberFormatException) {
            editMessageState.price.value = null
        }
    }

    fun onMessageFileChanged(filepath: Path) {
        editMessageState.attachmentInfo.value = AttachmentInfo(
            filePath = filepath,
            mediaType = filepath.deduceMediaType(),
            fileName = filepath.name.toFileName(),
            isLocalFile = true
        )
    }

    fun resetMessageFile() {
        editMessageState.attachmentInfo.value = null

    }

    private var sendMessageJob: Job? = null
    fun onSendMessage() {
        if (sendMessageJob?.isActive == true) {
            return
        }

        sendMessageJob = scope.launch(dispatchers.mainImmediate) {
            val sendMessageBuilder = SendMessage.Builder()
                .setChatId(editMessageState.chatId)
                .setContactId(editMessageState.contactId)
                .setText(editMessageState.messageText.value.text.trim())
                .setPaidMessagePrice(editMessageState.price.value?.toSat())
                .also { builder ->
                    editMessageState.replyToMessage.value?.message?.uuid?.value?.toReplyUUID().let { replyUUID ->
                        builder.setReplyUUID(replyUUID)
                    }
                }

            if (
                editMessageState.price?.value ?: 0 > 0 &&
                editMessageState.messageText.value.text.isNotEmpty()
            ) {
                //Paid text message
                createPaidMessageFile(editMessageState.messageText.value.text.trim())?.let { path ->
                    sendMessageBuilder.setAttachmentInfo(
                        AttachmentInfo(
                            filePath = path,
                            mediaType = MediaType.Text,
                            path.name.toFileName(),
                            isLocalFile = true,
                        )
                    )
                }
            }

            editMessageState.attachmentInfo.value?.let { attachmentInfo ->
                sendMessageBuilder.setAttachmentInfo(attachmentInfo)
            }

            val sendMessage = sendMessageBuilder.build()

            if (sendMessage.first != null) {
                sendMessage.first?.let { message ->
                    messageRepository.sendMessage(message)

                    setEditMessageState {
                        initialState()
                    }

                    delay(200L)
                    onNewMessageCallback?.invoke()
                }
            } else if (sendMessage.second != null) {
                toast("Message Validation failed: ${sendMessage.second?.name}", primary_red)
            }
        }
    }

    fun sendCallInvite(audioOnly: Boolean) {
        SphinxCallLink.newCallInvite(null, audioOnly)?.value?.let { newCallLink ->
            editMessageState.messageText.value = TextFieldValue(newCallLink)
            editMessageState.price.value = null

            onSendMessage()
        }
    }

    private var toggleChatMutedJob: Job? = null
    fun toggleChatMuted() {
        if (toggleChatMutedJob?.isActive == true) {
            return
        }

        chatSharedFlow.replayCache.firstOrNull()?.let { chat ->
            toggleChatMutedJob = scope.launch(dispatchers.mainImmediate) {
                if (chat.isTribe()) {
                    _notificationLevelStateFlow.value = Pair(true, chat.notifyActualValue())
                    return@launch
                }

                val newLevel = if (chat.notifyActualValue().isMuteChat()) NotificationLevel.SeeAll else NotificationLevel.MuteChat

                Exhaustive@
                when (val response = chatRepository.setNotificationLevel(chat, newLevel)) {
                    is Response.Error -> {
                        toast(response.cause.message, color = primary_red)
                    }
                    is Response.Success -> {
                        if (response.value) {
                            toast(
                                message = "Chat is now muted. You won\'t get push notifications\nfor incoming messages on this chat",
                                delay = 3000L
                            )
                        }
                    }
                }
            }
        }
    }

    private var toggleNotificationLevelJob: Job? = null
    fun setNotificationLevel(level: NotificationLevel) {
        if (toggleNotificationLevelJob?.isActive == true) {
            return
        }

        _notificationLevelStateFlow.value = Pair(true, level)

        toggleNotificationLevelJob = scope.launch(dispatchers.mainImmediate) {
            getChat()?.let { chat ->
                val response = chatRepository.setNotificationLevel(chat, level)

                if (response is Response.Error) {
                    toast(response.cause.message, color = primary_red)
                    _notificationLevelStateFlow.value = Pair(true, chat.notifyActualValue())
                }
            }
        }
    }

    fun closeNotificationLevelPopup() {
        _notificationLevelStateFlow.value = Pair(false, null)
    }

    private suspend fun createPaidMessageFile(text: String?): Path? {
        if (text.isNullOrEmpty()) {
            return null
        }
        return try {
            val output = mediaCacheHandler.createPaidTextFile("txt")
            mediaCacheHandler.copyTo(text.byteInputStream(), output)
        } catch (e: IOException) {
            null
        }
    }

    private var payAttachmentJob: Job? = null
    fun payAttachment(message: Message) {
        if (payAttachmentJob?.isActive == true) {
            return
        }

        confirm(
            "Confirm Purchase",
            "Are you sure you want to purchase this item?"
        ) {
            payAttachmentJob = scope.launch(dispatchers.mainImmediate) {

                Exhaustive@
                when (val response = messageRepository.payAttachment(message)) {
                    is Response.Error -> {
                        toast(response.cause.message, color = primary_red)
                    }
                    is Response.Success -> {}
                }
            }
        }
    }

    fun downloadFileMedia(message: Message, sent: Boolean) {
        repositoryMedia.downloadMediaIfApplicable(message, sent)
    }

    private suspend fun handleLinkPreview(link: LinkSpec): ChatMessage.LinkPreview? {
        var preview: ChatMessage.LinkPreview? = null

        scope.launch(dispatchers.mainImmediate) {
            Exhaustive@
            when (link.tag) {
                LinkTag.LightningNodePublicKey.name, LinkTag.VirtualNodePublicKey.name -> {
                    (link.url.toLightningNodePubKey() ?: link.url.toVirtualLightningNodeAddress())?.let { nodeDescriptor ->
                        ((nodeDescriptor as? LightningNodePubKey) ?: (nodeDescriptor as? VirtualLightningNodeAddress)?.getPubKey())?.let { pubKey ->
                            val existingContact: Contact? = contactRepository.getContactByPubKey(pubKey).firstOrNull()

                            if (existingContact != null) {
                                preview = ChatMessage.LinkPreview.ContactPreview(
                                    alias = existingContact.alias,
                                    photoUrl = existingContact.photoUrl,
                                    showBanner = false,
                                    lightningNodeDescriptor = nodeDescriptor
                                )
                            } else {
                                preview = ChatMessage.LinkPreview.ContactPreview(
                                    alias = null,
                                    photoUrl = null,
                                    showBanner = true,
                                    lightningNodeDescriptor = nodeDescriptor
                                )
                            }
                        }
                    }
                }
                LinkTag.JoinTribeLink.name -> {
                    link.url.toTribeJoinLink()?.let { tribeJoinLink ->
                        try {
                            val uuid = ChatUUID(tribeJoinLink.tribeUUID)

                            val thisChat = getChat()
                            if (thisChat?.uuid == uuid) {

                                preview = ChatMessage.LinkPreview.TribeLinkPreview(
                                    name = TribePreviewName(thisChat.name?.value ?: ""),
                                    description = null,
                                    imageUrl = thisChat.photoUrl?.toPreviewImageUrlOrNull(),
                                    showBanner = true,
                                    joinLink = tribeJoinLink
                                )

                            } else {
                                val existingChat = chatRepository.getChatByUUID(uuid).firstOrNull()
                                if (existingChat != null) {

                                    preview = ChatMessage.LinkPreview.TribeLinkPreview(
                                        name = TribePreviewName(existingChat.name?.value ?: ""),
                                        description = null,
                                        imageUrl = existingChat.photoUrl?.toPreviewImageUrlOrNull(),
                                        showBanner = false,
                                        joinLink = tribeJoinLink,
                                    )

                                } else {

                                    val tribePreview = linkPreviewHandler.retrieveTribeLinkPreview(tribeJoinLink)

                                    if (tribePreview != null) {
                                        preview = ChatMessage.LinkPreview.TribeLinkPreview(
                                            name = tribePreview.name,
                                            description = tribePreview.description,
                                            imageUrl = tribePreview.imageUrl,
                                            showBanner = true,
                                            joinLink = tribeJoinLink,
                                        )
                                    } // else do nothing
                                }
                            }
                        } catch (_: Exception) {
                            // no - op
                        }
                    }
                }
                LinkTag.WebURL.name -> {
                    val htmlPreview = linkPreviewHandler.retrieveHtmlPreview(link.url)

                    if (htmlPreview != null) {
                        preview = ChatMessage.LinkPreview.HttpUrlPreview(
                            title = htmlPreview.title,
                            domainHost = htmlPreview.domainHost,
                            description = htmlPreview.description,
                            imageUrl = htmlPreview.imageUrl,
                            favIconUrl = htmlPreview.favIconUrl,
                            url = link.url
                        )
                    }
                }
            }
        }.join()

        return preview
    }

    fun tribeLinkClicked(link: TribeJoinLink?) {
        scope.launch(dispatchers.mainImmediate) {
            link?.let {
                it.tribeUUID?.toChatUUID()?.let { chatUUID ->
                    chatRepository.getChatByUUID(chatUUID).firstOrNull()?.let { chat ->
                        getDashboardChatFor(null, chat)?.let { dashboardChat ->
                            ChatDetailState.screenState(
                                ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail(
                                    chat.id,
                                    dashboardChat
                                )
                            )
                            selectListRowFor(dashboardChat)
                        }
                    } ?: run {
                        dashboardViewModel.toggleJoinTribeWindow(true, it)
                    }
                }
            }
        }
    }

    fun contactLinkClicked(link: LightningNodeDescriptor?) {
        scope.launch(dispatchers.mainImmediate) {
            ((link as? LightningNodePubKey) ?: (link as? VirtualLightningNodeAddress)?.getPubKey())?.let { publicKey ->
                contactRepository.getContactByPubKey(publicKey).firstOrNull()?.let { contact ->
                    val chat = repositoryDashboard.getConversationByContactIdFlow(contact.id).firstOrNull()
                    chat?.let {
                        getDashboardChatFor(contact, chat)?.let { dashboardChat ->
                            ChatDetailState.screenState(
                                ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail(
                                    chat?.id,
                                    contact.id,
                                    dashboardChat
                                )
                            )
                            selectListRowFor(dashboardChat)
                        }
                    } ?: run {
                        getDashboardChatFor(contact, null)?.let { dashboardChat ->
                            ChatDetailState.screenState(
                                ChatDetailData.SelectedChatDetailData.SelectedContactDetail(
                                    contact.id,
                                    dashboardChat
                                )
                            )
                            selectListRowFor(dashboardChat)
                        }
                    }
                } ?: run {
                    dashboardViewModel.toggleContactWindow(true, ContactScreenState.AlreadyOnSphinx(link))
                }
            }
        }
    }

    private fun selectListRowFor(dashboardChat: DashboardChat) {
        (ChatListState.screenState() as? ChatListData.PopulatedChatListData)?.let { currentState ->
            ChatListState.screenState(
                ChatListData.PopulatedChatListData(
                    currentState.dashboardChats,
                    dashboardChat.dashboardChatId
                )
            )
        }
    }

    private suspend fun getDashboardChatFor(contact: Contact?, chat: Chat?): DashboardChat? {
        chat?.let { nnChat ->
            val message: Message? = nnChat.latestMessageId?.let {
                repositoryDashboard.getMessageById(it).firstOrNull()
            }

            val owner = getOwner()
            val color = getColorFor(contact, chat)
            val unseenMessagesFlow = repositoryDashboard.getUnseenMessagesByChatId(chat.id)

            if (nnChat.isTribe()) {
                val unseenMentionsFlow = repositoryDashboard.getUnseenMentionsByChatId(chat.id)

                return DashboardChat.Active.GroupOrTribe(
                    chat,
                    message,
                    owner,
                    color,
                    unseenMessagesFlow,
                    unseenMentionsFlow
                )
            } else {
                contact?.let { nnContact ->
                    return DashboardChat.Active.Conversation(
                        chat,
                        message,
                        nnContact,
                        color,
                        unseenMessagesFlow
                    )
                }
            }
        } ?: contact?.let { nnContact ->
            return DashboardChat.Inactive.Conversation(
                nnContact,
                getColorFor(nnContact, null),
            )
        }
        return null
    }

    fun toast(
        message: String,
        color: Color = primary_green,
        delay: Long = 2000L
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.toast(
                "Sphinx",
                message,
                color.value,
                delay
            )
        }
    }

    private fun confirm(
        title: String,
        message: String,
        callback: () -> Unit
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.confirmAlert(
                "Sphinx",
                title,
                message,
                callback
            )
        }
    }

    @ColorInt
    suspend fun getColorFor(
        contact: Contact?,
        chat: Chat?
    ): Int? {
        (contact?.getColorKey() ?: chat?.getColorKey())?.let { colorKey ->
            return colorsHelper.getColorIntForKey(
                colorKey,
                Integer.toHexString(getRandomColorRes().hashCode())
            )
        }
        return null
    }
}