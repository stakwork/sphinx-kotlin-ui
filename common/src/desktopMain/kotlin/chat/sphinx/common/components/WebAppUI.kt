package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ColorScheme
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
import chat.sphinx.common.state.AuthorizeViewState
import chat.sphinx.common.state.ContentState.windowState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.JsMessageHandler
import chat.sphinx.common.viewmodel.WebAppViewModel
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
import theme.semi_transparent_background

@Composable
fun WebAppUI(
    webAppViewModel: WebAppViewModel,
    chatViewModel: ChatViewModel?
) {
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        Window(
            onCloseRequest = {
                webAppViewModel.toggleWebAppWindow(false, null)
            },
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
                Text(
                    text = "Loading. Please wait...",
                    maxLines = 1,
                    fontSize = 14.sp,
                    fontFamily = Roboto,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.align(Alignment.Center)
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val webViewState by webAppViewModel.webViewStateFlow.collectAsState()
                    webViewState?.let { url ->
                        MaterialTheme {
                            val webViewState = rememberWebViewState(url)
                            val webViewNavigator = webAppViewModel.customWebViewNavigator
                            val jsBridge = webAppViewModel.customJsBridge

                            initWebView(webViewState)
                            initJsBridge(jsBridge, webAppViewModel)

                            Column(Modifier.fillMaxSize()) {
                                WebView(
                                    state = webViewState,
                                    modifier = Modifier.fillMaxSize(),
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

@Composable
fun AuthorizeViewUI(
    webAppViewModel: WebAppViewModel,
    budgetField: Boolean
) {
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        Window(
            onCloseRequest = { webAppViewModel.closeAuthorizeView() },
            title = if (budgetField) "Set Budget" else "Authorize",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(200, 200)
            ),
            alwaysOnTop = false,
            resizable = true,
            focusable = true,
        ) {
//            Box(
//                modifier = Modifier.fillMaxSize()
//                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
//            ) {
//
//            }
        }
    }
}

fun initWebView(webViewState: WebViewState) {
    webViewState.webSettings.apply {
        zoomLevel = 1.0
        isJavaScriptEnabled = true
        customUserAgentString = "Sphinx"
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = true
            allowFileAccess = true
        }
    }
}

fun initJsBridge(
    webViewJsBridge: WebViewJsBridge,
    webAppViewModel: WebAppViewModel
) {
    webViewJsBridge.register(
        JsMessageHandler(webAppViewModel)
    )
}