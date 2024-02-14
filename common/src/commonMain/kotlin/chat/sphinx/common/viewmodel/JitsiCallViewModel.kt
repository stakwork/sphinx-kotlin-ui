package chat.sphinx.common.viewmodel

import chat.sphinx.concepts.network.query.lightning.model.lightning.*
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.*
import chat.sphinx.wrapper.bridge.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JitsiCallViewModel {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val viewModelScope = SphinxContainer.appModule.applicationScope

    private val _jitsiCallWindowStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    private val _webViewStateFlow: MutableStateFlow<String?> by lazy {
        MutableStateFlow(null)
    }

    val jitsiCallWindowStateFlow: StateFlow<Boolean>
        get() = _jitsiCallWindowStateFlow.asStateFlow()

    val webViewStateFlow: StateFlow<String?>
        get() = _webViewStateFlow.asStateFlow()

    fun toggleJitsiCallWindow(
        open: Boolean,
        url: String?
    ) {
        if (_jitsiCallWindowStateFlow.value != open) {
            _jitsiCallWindowStateFlow.value = open
        }

        if (!open) {
            return
        }

        viewModelScope.launch(dispatchers.io) {
            delay(1000L)

            toggleWebViewWindow(url)
        }
    }

    private fun toggleWebViewWindow(url: String?) {
        url?.let { nnUrl ->
            _webViewStateFlow.value = nnUrl
        }
    }
}