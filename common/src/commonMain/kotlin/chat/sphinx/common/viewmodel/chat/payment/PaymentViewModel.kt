package chat.sphinx.common.viewmodel.chat.payment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ChatPaymentState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.concepts.repository.message.model.SendPayment
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.meme_server.AuthenticationToken
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.message.MessageUUID
import chat.sphinx.wrapper.message.media.token.MediaHost
import chat.sphinx.wrapper.payment.PaymentTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PaymentViewModel(
    val chatViewModel: ChatViewModel,
    val mode: PaymentMode = PaymentMode.SEND
) {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val messageRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).messageRepository

    private val memeInputStreamHandler = SphinxContainer.networkModule.memeInputStreamHandler
    private val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler

    private val sendPaymentBuilder = SendPayment.Builder()

    private var paymentData: PaymentData? = null

    fun setPaymentData(paymentData: PaymentData?) {
        this.paymentData = paymentData
    }

    fun getPaymentData() : PaymentData? {
        return this.paymentData
    }

    enum class PaymentMode {
        SEND, RECEIVE
    }

    data class PaymentData(
        val chatId: ChatId? = null,
        val contactId: ContactId? = null,
        val messageUUID: MessageUUID? = null
    )

    val messageSharedFlow: SharedFlow<Message?> = flow {
        paymentData?.messageUUID?.let { messageUUID ->
            emitAll(messageRepository.getMessageByUUID(messageUUID))
        }
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1
    )

    var chatPaymentState: ChatPaymentState by mutableStateOf(initialState())

    fun initialState(): ChatPaymentState = ChatPaymentState(
        chatId = paymentData?.chatId,
        contactId = paymentData?.contactId,
        messageUUID = paymentData?.messageUUID
    )

    private inline fun setChatPaymentState(update: ChatPaymentState.() -> ChatPaymentState) {
        chatPaymentState = chatPaymentState.update()
    }

    fun resetChatPaymentState() {
        chatPaymentState = initialState()
    }

    fun onMessageChanged(text: String) {
        setChatPaymentState {
        copy(
            message = text,

            )
        }
    }

    fun onAmountTextChanged(text: String) {
        var amount = try {
            text.toLong()
        } catch (e: NumberFormatException) {
            0
        }
        setChatPaymentState {
            copy(
                amount = amount,
                saveButtonEnabled = amount > 0
            )
        }
    }

    private var sendPaymentJob: Job? = null
    fun sendPayment() {
        if (sendPaymentJob?.isActive == true) {
            return
        }

        sendPaymentJob = scope.launch(dispatchers.mainImmediate) {
            if (paymentData?.chatId != null && paymentData?.messageUUID != null) {
                sendTribeDirectPayment()
            } else if (paymentData?.contactId != null) {
                chatViewModel.toggleChatActionsPopup(
                    ChatViewModel.ChatActionsMode.SEND_TEMPLATE,
                    PaymentData(
                        paymentData?.chatId,
                        paymentData?.contactId
                    )
                )
            }
        }
    }

    private var sendContactPaymentJob: Job? = null
    fun sendContactPayment() {
        if (sendContactPaymentJob?.isActive == true) {
            return
        }

        if ((chatPaymentState.amount ?: 0) <= 0) return

        sendPaymentBuilder.setAmount(chatPaymentState.amount ?: 0)
        sendPaymentBuilder.setChatId(paymentData?.chatId)
        sendPaymentBuilder.setContactId(paymentData?.contactId)
        sendPaymentBuilder.setText(chatPaymentState.message)

        setChatPaymentState {
            copy(
                status = LoadResponse.Loading
            )
        }

        scope.launch(dispatchers.mainImmediate) {
            val sendPayment = sendPaymentBuilder.build()

            when (val response = messageRepository.sendPayment(sendPayment)) {
                is Response.Error -> {
                    setChatPaymentState {
                        copy(
                            status = response
                        )
                    }
                }
                is Response.Success -> {
                    chatViewModel.hideChatActionsPopup()
                }
            }
        }
    }

    private suspend fun sendTribeDirectPayment() {
        if ((chatPaymentState.amount ?: 0) <= 0) return

        setChatPaymentState {
            copy(
                status = LoadResponse.Loading
            )
        }

        scope.launch(dispatchers.mainImmediate) {
            messageRepository.sendTribePayment(
                chatId = paymentData?.chatId!!,
                amount = Sat(chatPaymentState.amount ?: 0),
                messageUUID = paymentData?.messageUUID!!,
                text = chatPaymentState.message
            )
        }.join()

        delay(500L)
        chatViewModel.hideChatActionsPopup()
    }

    init {
        scope.launch(dispatchers.mainImmediate) {
            loadTemplateImages()
        }
    }

    private val _paymentTemplateList: MutableStateFlow<List<PaymentTemplate>?> by lazy {
        MutableStateFlow(listOf())
    }

    val paymentTemplateList: StateFlow<List<PaymentTemplate>?>
        get() = _paymentTemplateList.asStateFlow()


    private var loadTemplateImagesJob: Job? = null
    private fun loadTemplateImages() {
        if (loadTemplateImagesJob?.isActive == true) {
            return
        }

        loadTemplateImagesJob = scope.launch(dispatchers.mainImmediate) {
            when (val response = messageRepository.getPaymentTemplates()) {
                is Response.Error -> {}
                is Response.Success -> {
                    _paymentTemplateList.value = setLocalFilePaymentTemplate(response.value)
                }
            }
        }
    }

    private suspend fun setLocalFilePaymentTemplate(templateList: List<PaymentTemplate>): List<PaymentTemplate> {
        templateList.forEach { template ->
            retrieveTemplateMediaInputStream(template)
        }
        return templateList
    }

    private suspend fun retrieveTemplateMediaInputStream(
        paymentTemplate: PaymentTemplate
    ) {
        val token = AuthenticationToken(paymentTemplate.token)

        paymentTemplate.getTemplateUrl(MediaHost.DEFAULT.value).let { url ->
            memeInputStreamHandler.retrieveMediaInputStream(
                url,
                token,
                null
            )?.first
        }?.let { inputStream ->
            mediaCacheHandler.createImageFile("jpg").let { imageFilepath ->
                imageFilepath.toFile().outputStream().use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)

                    paymentTemplate.localFile = imageFilepath.toFile()
                }
            }
        }
    }
    fun selectTemplate(position: Int) {
        paymentTemplateList.value?.getOrNull(position)?.let { template ->
            sendPaymentBuilder.setPaymentTemplate(template)
        }
    }
}
