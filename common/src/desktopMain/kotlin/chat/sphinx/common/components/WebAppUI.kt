package chat.sphinx.common.components

import CommonButton
import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.state.AuthorizeViewState
import chat.sphinx.common.viewmodel.JsMessageHandler
import chat.sphinx.common.viewmodel.WebAppViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.getPreferredWindowSize
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewState
import theme.*

@Composable
fun WebAppUI(
    webAppViewModel: WebAppViewModel,
    chatViewModel: ChatViewModel?
) {
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        println("WebViewWindow recompose")
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
                size = getPreferredWindowSize(300, if (budgetField) 450 else 350)
            ),
            alwaysOnTop = false,
            resizable = true,
            focusable = true,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        Modifier.fillMaxWidth().height(55.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = "Verified",
                                tint = primary_blue,
                                modifier = Modifier.size(55.dp)
                            )
                        }
                    )
                    Text(
                        text = "AUTHORIZE",
                        fontSize = 20.sp,
                        color = sphinx_action_menu,
                        fontWeight = FontWeight.W400,
                        fontFamily = Roboto,
                        modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = (webAppViewModel.authorizeViewStateFlow.value as? AuthorizeViewState.Opened)?.url ?: "test",
                        fontSize = 17.sp,
                        color = md_theme_dark_tertiary,
                        fontWeight = FontWeight.W400,
                        fontFamily = Roboto,
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp),
                        textAlign = TextAlign.Center
                    )
                    if (budgetField) {
                        Column(
                            modifier = Modifier.fillMaxWidth().height(170.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = "to withdraw up to",
                                fontSize = 15.sp,
                                color = sphinx_action_menu,
                                fontWeight = FontWeight.W400,
                                fontFamily = Roboto,
                                modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 15.dp),
                                textAlign = TextAlign.Center
                            )
                            OutlinedTextField(
                                shape = RoundedCornerShape(28.dp),
                                value = webAppViewModel.budgetState?.toString() ?: "",
                                onValueChange = {
                                    webAppViewModel.onAmountTextChanged(it)
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontFamily = Roboto
                                ),
                                placeholder = {
                                    Text(
                                        "0",
                                        modifier = Modifier.fillMaxWidth(),
                                        color = place_holder_text,
                                        fontFamily = Roboto,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = light_divider,
                                    unfocusedBorderColor = light_divider,
                                    cursorColor = primary_blue
                                )
                            )
                            Text(
                                text = "sats before reauthorizing",
                                fontSize = 15.sp,
                                color = sphinx_action_menu,
                                fontWeight = FontWeight.W400,
                                fontFamily = Roboto,
                                modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        CommonButton("AUTHORIZE", fontWeight = FontWeight.W500) {
                            if (budgetField) {
                                webAppViewModel.authorizeBudget()
                            } else {
                                webAppViewModel.authorizeApp()
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