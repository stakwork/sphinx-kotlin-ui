package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.AuthorizeViewState
import chat.sphinx.concepts.network.query.contact.model.PersonDataDto
import chat.sphinx.concepts.network.query.lightning.model.lightning.*
import chat.sphinx.concepts.network.query.message.model.PutPaymentRequestDto
import chat.sphinx.concepts.repository.message.model.SendPayment
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.clazzes.PasswordGenerator
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.*
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.bridge.*
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.lightning.Bolt11
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.toLightningPaymentRequestOrNull
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import theme.badge_red

class WebAppViewModel {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val viewModelScope = SphinxContainer.appModule.applicationScope
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val lightningRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).lightningRepository
    private val messageRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).messageRepository

    companion object {
        const val APPLICATION_NAME = "Sphinx"

        const val TYPE_AUTHORIZE = "AUTHORIZE"
        const val TYPE_SETBUDGET = "SETBUDGET"
        const val TYPE_GETBUDGET = "GETBUDGET"
        const val TYPE_LSAT = "LSAT"
        const val TYPE_GETLSAT = "GETLSAT"
        const val TYPE_SIGN = "SIGN"
        const val TYPE_KEYSEND = "KEYSEND"
        const val TYPE_UPDATELSAT = "UPDATELSAT"
        const val TYPE_PAYMENT = "PAYMENT"
        const val TYPE_UPDATED = "UPDATED"
        const val TYPE_GETPERSONDATA = "GETPERSONDATA"
    }

    private val sendPaymentBuilder = SendPayment.Builder()

    private var password = generatePassword()
    private var budget: Int? = null

    var callback: ((String) -> Unit)? = null

    private val _webAppWindowStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    private val _webViewStateFlow: MutableStateFlow<String?> by lazy {
        MutableStateFlow(null)
    }

    private val _authorizeViewStateFlow: MutableStateFlow<AuthorizeViewState> by lazy {
        MutableStateFlow(AuthorizeViewState.Closed())
    }

    val webAppWindowStateFlow: StateFlow<Boolean>
        get() = _webAppWindowStateFlow.asStateFlow()

    val webViewStateFlow: StateFlow<String?>
        get() = _webViewStateFlow.asStateFlow()

    val authorizeViewStateFlow: StateFlow<AuthorizeViewState>
        get() = _authorizeViewStateFlow.asStateFlow()

    fun toggleWebAppWindow(
        open: Boolean,
        url: String?
    ) {
        if (_webAppWindowStateFlow.value != open) {
            _webAppWindowStateFlow.value = open
        }

        if (!open) {
            return
        }

        viewModelScope.launch(dispatchers.io) {
            delay(1000L)

            toggleWebViewWindow(url)
        }
    }

    private fun toggleAuthorizeView() {
        _webViewStateFlow?.value?.let { url ->
            _authorizeViewStateFlow.value = AuthorizeViewState.Opened(url, false)
        }
    }

    private fun toggleSetBudgetView() {
        _webViewStateFlow?.value?.let { url ->
            _authorizeViewStateFlow.value = AuthorizeViewState.Opened(url, true)
        }
    }

    fun closeAuthorizeView() {
        _authorizeViewStateFlow.value = AuthorizeViewState.Closed()
    }

    val customWebViewNavigator : WebViewNavigator
        get() {
            return WebViewNavigator(CoroutineScope(Dispatchers.IO))
        }

    val customJsBridge : WebViewJsBridge
        get() {
            return WebViewJsBridge(customWebViewNavigator)
        }

    private fun evaluateJavascript(script: String) {
//        val completeScript = "window.sphinxMessage(\'$script\')"
//
//        println(completeScript)
//
//        customWebViewNavigator?.evaluateJavaScript(completeScript, ({ result ->
//            println("EVALUATE JAVASCRIPT RESULT $result")
//        }))
    }

    fun onJsBridgeMessageReceived(
        message: JsMessage,
        callback: (String) -> Unit
    ) {
        this.callback = callback

        println("MESSAGE RECEIVED: $message")

        viewModelScope.launch(dispatchers.mainImmediate) {

            message.params.toBridgeAuthorizeMessageOrNull()?.let {
                if (it.type == TYPE_AUTHORIZE) {
//                toggleAuthorizeView()
                    authorizeApp()
                }
            }

            message.params.toBridgeSetBudgetMessageOrNull()?.let {
                if (it.type == TYPE_SETBUDGET) {
//                    toggleSetBudgetView()
                    setBudget(100)
                }
            }

            message.params.toBridgeGetLSATMessageOrNull()?.let {
                if (it.type == TYPE_GETLSAT) {
//                    toggleSetBudgetView()
                    getActiveLSAT(it)
                }
            }

            message.params.toBridgeSignMessageOrNull()?.let {
                if (it.type == TYPE_SIGN) {
                    signChallenge(it)
                }
            }

            message.params.toBridgeKeysendMessageOrNull()?.let {
                if (it.type == TYPE_KEYSEND) {
                    sendKeysend(it)
                }
            }

            message.params.toBridgeGetBudgetMessageOrNull()?.let {
                if (it.type == TYPE_GETBUDGET) {
                    sendGetBudgetMessage()
                }
            }

            message.params.toBridgeLSatMessage()?.let {
                if (it.type == TYPE_LSAT) {
                    payLSat(it)
                }
            }

            message.params.toBridgeUpdateLSatMessageOrNull()?.let {
                if (it.type == TYPE_UPDATELSAT) {
                    updateLSat(it)
                }
            }

            message.params.toBridgePaymentMessageOrNull()?.let {
                if (it.type == TYPE_PAYMENT) {
                    sendPayment(it)
                }
            }

            message.params.toBridgeUpdatedMessageOrNull()?.let {
                if (it.type == TYPE_UPDATED) {
                    sendUpdatedMessage()
                }
            }

            message.params.toBridgeGetPersonDataMessageOrNull()?.let {
                if (it.type == TYPE_GETPERSONDATA) {
                    getPersonData()
                }
            }
        }
    }

    private fun toggleWebViewWindow(url: String?) {
        url?.let { nnUrl ->
            _webViewStateFlow.value = nnUrl
        }

//        viewModelScope.launch(dispatchers.mainImmediate) {
//            openAuthorize()
//        }
    }

    private suspend fun openAuthorize() {
        delay(15000L)

        toggleAuthorizeView()
    }

    private suspend fun authorizeApp() {
        delay(1000L)

        _webViewStateFlow?.value?.let { url ->

            getOwner().nodePubKey?.value?.let { pubkey ->
                this.password = generatePassword()

                val message = BridgeMessage(
                    budget = null,
                    pubkey = pubkey,
                    type = TYPE_AUTHORIZE,
                    password = password,
                    application = APPLICATION_NAME,
                    signature = null
                ).toJson()

                callback?.let {
                    it(message)
                }

                callback = null
            }
        }
    }

    private suspend fun setBudget(amount: Int) {
        delay(1000L)

        _webViewStateFlow?.value?.let { url ->

            getOwner().nodePubKey?.value?.let { pubkey ->
                this.budget = amount
                this.password = generatePassword()

                val message = BridgeMessage(
                    pubkey = pubkey,
                    type = TYPE_SETBUDGET,
                    password = password,
                    application = APPLICATION_NAME,
                    budget = amount,
                    signature = null
                ).toJson()

                callback?.let {
                    it(message)
                }

                callback = null
            }
        }
    }

    private fun getActiveLSAT(getLSATMessage: BridgeGetLSATMessage) {
        getLSATMessage.issuer?.let {
            lightningRepository.getActiveLSat(it).collect { loadResponse: LoadResponse<ActiveLsatDto, ResponseError> ->
                Exhaustive@
                when (loadResponse) {
                    is LoadResponse.Loading -> {}
                    is Response.Error -> {
                        sendActiveLSAT(null, true)
                    }
                    is Response.Success -> {
                        (loadResponse.value as? ActiveLsatDto)?.let {
                            sendActiveLSAT(it, true)
                        } ?: run {
                            sendActiveLSAT(null, true)
                        }
                    }
                }
            }
        }
    }

    private fun sendActiveLSAT(
        activeLSatDto: ActiveLsatDto?,
        success: Boolean
    ) {
        activeLSatDto?.let {
            this.password = generatePassword()

            val message = SendActiveLSatMessage(
                TYPE_GETLSAT,
                APPLICATION_NAME,
                password,
                it.macaroon,
                it.paymentRequest,
                it.preimage,
                it.identifier,
                it.issuer,
                success,
                it.status,
                it.paths ?: ""
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        } ?: run {
            this.password = generatePassword()

            val message = SendActiveLSatFailedMessage(
                TYPE_GETLSAT,
                APPLICATION_NAME,
                password,
                success
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        }
    }

    private fun signChallenge(
        bridgeSignMessage: BridgeSignMessage
    ) {
        lightningRepository.signChallenge(bridgeSignMessage.message).collect { loadResponse: LoadResponse<SignChallengeDto, ResponseError> ->
            Exhaustive@
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    sendActiveLSAT(null, true)
                }
                is Response.Success -> {
                    (loadResponse.value as? SignChallengeDto)?.let {
                        sendSignMessage(it, true)
                    } ?: run {
                        sendSignMessage(null, true)
                    }
                }
            }
        }
    }

    private fun sendKeysend(
        keysendMessage: BridgeKeysendMessage
    ) {
        if (checkCanPay(keysendMessage.amt)) {
            sendPaymentBuilder.setAmount(keysendMessage.amt.toLong())
            sendPaymentBuilder.setDestinationKey(LightningNodePubKey(keysendMessage.dest))

            val sendPayment = sendPaymentBuilder.build()
            val response : Response<Any, ResponseError> = messageRepository.sendPayment(sendPayment)

            when (response) {
                is Response.Error -> {
                    sendKeysendMessage(keysendMessage, false)
                }
                is Response.Success -> {
                    sendKeysendMessage(keysendMessage, true)
                }
            }
        } else {
            sendKeysendMessage(keysendMessage, false)
        }
    }

    private fun sendGetBudgetMessage() {
        this.password = generatePassword()

        val message = SendGetBudgetMessage(
            TYPE_GETBUDGET,
            APPLICATION_NAME,
            password,
            budget ?: 0,
            true
        ).toJson()

        callback?.let {
            it(message)
        }

        callback = null
    }

    private fun sendKeysendMessage(
        keysendMessage: BridgeKeysendMessage,
        success: Boolean
    ) {
        keysendMessage?.let {
            this.password = generatePassword()

            val message = SendKeysendMessage(
                TYPE_KEYSEND,
                APPLICATION_NAME,
                password,
                success
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        }
    }

    private fun checkCanPay(
        amount: Int
    ) : Boolean {
        if (budget != null && budget!! > amount) {
            budget = budget!! - amount
            return true
        }
        return false
    }

    private fun sendSignMessage(
        signChallengeDto: SignChallengeDto?,
        success: Boolean
    ) {
        signChallengeDto?.let {
            this.password = generatePassword()

            val message = SendSignMessage(
                TYPE_SIGN,
                APPLICATION_NAME,
                password,
                it.sig,
                success
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        } ?: run {
            this.password = generatePassword()

            val message = SendFailedSignMessage(
                TYPE_SIGN,
                APPLICATION_NAME,
                password,
                false
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        }
    }

    private fun payLSat(lSatMessage: BridgeLSatMessage) {
        lSatMessage.paymentRequest.toLightningPaymentRequestOrNull()?.let {
            val decodedPaymentRequest = Bolt11.decode(it)

            decodedPaymentRequest.getSatsAmount()?.value?.toInt()?.let {amount ->
                if (checkCanPay(amount)) {
                    val payLsatSto = PayLsatDto(
                        lSatMessage.macaroon,
                        lSatMessage.paymentRequest,
                        lSatMessage.issuer
                    )

                    lightningRepository.payLSat(payLsatSto).collect { loadResponse: LoadResponse<PayLsatResponseDto, ResponseError> ->
                        Exhaustive@
                        when (loadResponse) {
                            is LoadResponse.Loading -> {}
                            is Response.Error -> {
                                sendLSat(lSatMessage, null, false)
                            }
                            is Response.Success -> {
                                sendLSat(lSatMessage, loadResponse.value.lsat, false)
                            }
                        }
                    }
                } else {
                    sendLSat(lSatMessage,null, false)
                }
            }
        }
    }

    private fun sendLSat(
        lSatMessage: BridgeLSatMessage,
        lsat: String?,
        success: Boolean
    ) {
        if (lsat != null && success) {
            this.password = generatePassword()

            val message = SendLSatMessage(
                TYPE_LSAT,
                APPLICATION_NAME,
                password,
                lsat,
                lSatMessage.paymentRequest,
                lSatMessage.macaroon,
                lSatMessage.issuer,
                budget,
                true
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        } else {
            this.password = generatePassword()

            val message = SendLSatFailedMessage(
                TYPE_LSAT,
                APPLICATION_NAME,
                password,
                lSatMessage.paymentRequest,
                lSatMessage.macaroon,
                lSatMessage.issuer,
                false
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        }
    }

    private fun updateLSat(updateLSatMessage: BridgeUpdateLSatMessage) {
        val updateLsatSto = UpdateLsatDto(
            updateLSatMessage.status
        )

        lightningRepository.updateLSat(updateLsatSto).collect { loadResponse: LoadResponse<PayLsatResponseDto, ResponseError> ->
            Exhaustive@
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    sendUpdateLSat(updateLSatMessage, null, false)
                }
                is Response.Success -> {
                    sendUpdateLSat(updateLSatMessage, loadResponse.value.lsat, false)
                }
            }
        }
    }

    private fun sendUpdateLSat(
        updateLSatMessage: BridgeUpdateLSatMessage,
        lsat: String?,
        success: Boolean
    ) {
        if (lsat != null && success) {
            this.password = generatePassword()

            val message = SendUpdateLSatMessage(
                TYPE_UPDATELSAT,
                APPLICATION_NAME,
                password,
                updateLSatMessage.identifier,
                updateLSatMessage.status,
                lsat,
                true
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        } else {
            this.password = generatePassword()

            val message = SendUpdateLSatFailedMessage(
                TYPE_UPDATELSAT,
                APPLICATION_NAME,
                password,
                updateLSatMessage.identifier,
                updateLSatMessage.status,
                false
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        }
    }

    private fun sendPayment(bridgePaymentMessage: BridgePaymentMessage) {
        bridgePaymentMessage.paymentRequest.toLightningPaymentRequestOrNull()?.let {
            val decodedPaymentRequest = Bolt11.decode(it)

            decodedPaymentRequest.getSatsAmount()?.value?.toInt()?.let {amount ->
                if (checkCanPay(amount)) {
                    val putPaymentRequestDto = PutPaymentRequestDto(
                        bridgePaymentMessage.paymentRequest
                    )

                    messageRepository.payPaymentRequest(putPaymentRequestDto).collect { loadResponse: LoadResponse<Any, ResponseError> ->
                        Exhaustive@
                        when (loadResponse) {
                            is LoadResponse.Loading -> {}
                            is Response.Error -> {
                                sendPaymentMessage(bridgePaymentMessage,false)
                            }
                            is Response.Success -> {
                                sendPaymentMessage(bridgePaymentMessage,true)
                            }
                        }
                    }
                } else {
                    sendPaymentMessage(bridgePaymentMessage,false)
                }
            }
        }
    }

    private fun sendPaymentMessage(
        bridgePaymentMessage: BridgePaymentMessage,
        success: Boolean
    ) {
        this.password = generatePassword()

        val message = SendPaymentMessage(
            TYPE_PAYMENT,
            APPLICATION_NAME,
            password,
            bridgePaymentMessage.paymentRequest,
            success
        ).toJson()

        callback?.let {
            it(message)
        }

        callback = null
    }

    private fun sendUpdatedMessage() {
        this.password = generatePassword()

        val message = SendUpdatedMessage(
            TYPE_UPDATED,
            APPLICATION_NAME,
            password
        ).toJson()

        callback?.let {
            it(message)
        }

        callback = null
    }

    private fun getPersonData() {
        contactRepository.getPersonData().collect { loadResponse: LoadResponse<PersonDataDto, ResponseError> ->
            Exhaustive@
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    sendPersonDataMessage(null, false)
                }
                is Response.Success -> {
                    sendPersonDataMessage(loadResponse.value, true)
                }
            }
        }
    }

    private fun sendPersonDataMessage(personData: PersonDataDto?, success: Boolean) {
        this.password = generatePassword()

        if (personData != null && success) {
            val message = SendPersonDataMessage(
                TYPE_GETPERSONDATA,
                APPLICATION_NAME,
                password,
                personData!!.publicKey,
                personData!!.alias,
                personData!!.photoUrl ?: "",
                success
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        } else {
            val message = SendPersonDataFailedMessage(
                TYPE_GETPERSONDATA,
                APPLICATION_NAME,
                password,
                success
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        }
    }

    private fun generatePassword(): String {
        @OptIn(RawPasswordAccess::class)
        return PasswordGenerator(passwordLength = 16).password.value.joinToString("")
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
}

class JsMessageHandler(
    private val webAppViewModel: WebAppViewModel
) : IJsMessageHandler {

    override fun methodName(): String {
        return "sphinx-bridge"
    }

    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        webAppViewModel.onJsBridgeMessageReceived(message, callback)
    }
}