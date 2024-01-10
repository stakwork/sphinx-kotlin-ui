package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.AuthorizeViewState
import chat.sphinx.concepts.network.query.lightning.model.lightning.ActiveLsatDto
import chat.sphinx.concepts.network.query.lightning.model.lightning.SignChallengeDto
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.clazzes.PasswordGenerator
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.*
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.bridge.*
import chat.sphinx.wrapper.contact.Contact
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WebAppViewModel {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val viewModelScope = SphinxContainer.appModule.applicationScope
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val lightningRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).lightningRepository

    companion object {
        const val APPLICATION_NAME = "Sphinx"

        const val TYPE_AUTHORIZE = "AUTHORIZE"
        const val TYPE_SETBUDGET = "SETBUDGET"
        const val TYPE_LSAT = "GETLSAT"
        const val TYPE_SIGN = "SIGN"
        const val TYPE_KEYSEND = "KEYSEND"
    }

    private val password = generatePassword()
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
                if (it.type == TYPE_LSAT) {
                    getActiveLSAT(it.issuer ?: "")
//                    toggleSetBudgetView()
                }
            }

            message.params.toBridgeSignMessageOrNull()?.let {
                if (it.type == TYPE_SIGN) {
                    signChallenge(it.message)
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

    private suspend fun getActiveLSAT(issuer: String) {
        delay(1000L)

        lightningRepository.getActiveLSat(issuer).collect { loadResponse: LoadResponse<ActiveLsatDto, ResponseError> ->
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

    private fun sendActiveLSAT(
        activeLSatDto: ActiveLsatDto?,
        success: Boolean
    ) {
        activeLSatDto?.let {
            val message = LSatMessage(
                TYPE_LSAT,
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
            val message = LSatFailedMessage(
                TYPE_LSAT,
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

    private suspend fun signChallenge(challenge: String) {
        delay(1000L)

        lightningRepository.signChallenge(challenge).collect { loadResponse: LoadResponse<SignChallengeDto, ResponseError> ->
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

    private fun sendSignMessage(
        signChallengeDto: SignChallengeDto?,
        success: Boolean
    ) {
        signChallengeDto?.let {
            val message = SendSignMessage(
                TYPE_SIGN,
                APPLICATION_NAME,
                it.sig,
                success
            ).toJson()

            callback?.let {
                it(message)
            }

            callback = null
        } ?: run {
            val message = SendFailedSignMessage(
                TYPE_LSAT,
                APPLICATION_NAME,
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