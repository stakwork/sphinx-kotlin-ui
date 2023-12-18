package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.state.ContentState.windowState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.JsMessageHandler
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.getPreferredWindowSize
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.util.KLogSeverity
import com.multiplatform.webview.web.*
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.delay

@Composable
fun WebAppUI(
    dashboardViewModel: DashboardViewModel,
    chatViewModel: ChatViewModel?
) {
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        Window(
            onCloseRequest = { dashboardViewModel.toggleWebAppWindow(false) },
            title = "Web App",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(1200, 800)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val webViewState by dashboardViewModel.webViewStateFlow.collectAsState()
                    if (webViewState) {
                        chatViewModel?.tribeDataStateFlow?.value?.appUrl?.value?.let { url ->
                            MaterialTheme {
                                val webViewState = rememberWebViewState(url)
                                val webViewNavigator = dashboardViewModel.customWebViewNavigator
                                val jsBridge = dashboardViewModel.customJsBridge

                                LaunchedEffect(Unit) {
                                    initWebView(webViewState)
                                    initJsBridge(jsBridge, dashboardViewModel)
                                }
                                Column(Modifier.fillMaxSize()) {
                                    WebView(
                                        state = webViewState,
                                        modifier = Modifier.fillMaxWidth(),
                                        navigator = webViewNavigator,
                                        webViewJsBridge = jsBridge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun initWebView(webViewState: WebViewState) {
    webViewState.webSettings.apply {
        zoomLevel = 1.0
        isJavaScriptEnabled = true
        customUserAgentString = "Sphinx"
        logSeverity = KLogSeverity.Verbose
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
            allowFileAccess = true
        }
    }
}

fun initJsBridge(
    webViewJsBridge: WebViewJsBridge,
    dashboardViewModel: DashboardViewModel
) {
    webViewJsBridge.register(
        JsMessageHandler(dashboardViewModel)
    )
}