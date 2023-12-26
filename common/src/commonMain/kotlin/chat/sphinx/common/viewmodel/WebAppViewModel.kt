package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.AuthorizeViewState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WebAppViewModel {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val viewModelScope = SphinxContainer.appModule.applicationScope
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository

    companion object {
        const val APPLICATION_NAME = "Sphinx"

        const val TYPE_AUTHORIZE = "AUTHORIZE"
        const val TYPE_SETBUDGET = "SETBUDGET"
        const val TYPE_LSAT = "LSAT"
        const val TYPE_KEYSEND = "KEYSEND"
    }

    val password = generatePassword()

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
        _webAppWindowStateFlow.value = open

        viewModelScope.launch(dispatchers.io) {
            delay(1000L)

            toggleWebViewWindow(url)
        }
    }

    private fun toggleAuthorizeView() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            authorizeApp()
        }

//        _webViewStateFlow?.value?.let { url ->
//            _authorizeViewStateFlow.value = AuthorizeViewState.Opened(url, false)
//        }
    }

    private fun toggleSetBudgetView() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            setBudget(100)
        }

//        _webViewStateFlow?.value?.let { url ->
//            _authorizeViewStateFlow.value = AuthorizeViewState.Opened(url, true)
//        }
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
        println("window.sphinxMessage($script)")

        customWebViewNavigator.evaluateJavaScript("window.sphinxMessage(\'$script\')") { result ->
            println(result)
        }
    }

    fun onJsBridgeMessageReceived(message: JsMessage) {
        println("MESSAGE RECEIVED: $message")

        message.params.toBridgeAuthorizeMessageOrNull()?.let {
            if (it.type == TYPE_AUTHORIZE) {
                toggleAuthorizeView()
            }
        }

        message.params.toBridgeSetBudgetMessageOrNull()?.let {
            if (it.type == TYPE_SETBUDGET) {
                toggleSetBudgetView()
            }
        }
    }

    private fun toggleWebViewWindow(url: String?) {
        url?.let { nnUrl ->
            _webViewStateFlow.value = nnUrl
        }
    }

    private suspend fun authorizeApp() {
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

                evaluateJavascript(message)
            }
        }
    }

    private suspend fun setBudget(amount: Int) {
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

                evaluateJavascript(message)
            }
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
        webAppViewModel.onJsBridgeMessageReceived(message)
    }
}