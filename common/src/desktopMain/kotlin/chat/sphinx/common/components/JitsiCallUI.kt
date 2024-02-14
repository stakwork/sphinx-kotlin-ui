package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.JitsiCallViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun JitsiCallUI(
    dashboardViewModel: DashboardViewModel,
    jitsiCallViewModel: JitsiCallViewModel
) {
    var isOpen by remember { mutableStateOf(true) }
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)

    if (isOpen) {
        Window(
            onCloseRequest = {
                jitsiCallViewModel.toggleJitsiCallWindow(false, null)
            },
            title = "Web App",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(1200, 800)
            ),
            icon = sphinxIcon
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
                    val webViewState by jitsiCallViewModel.webViewStateFlow.collectAsState()
                    webViewState?.let { url ->
                        MaterialTheme {
                            val webViewState = rememberWebViewState(url)

                            initWebView(webViewState)

                            Column(Modifier.fillMaxSize()) {
                                WebView(
                                    state = webViewState,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}