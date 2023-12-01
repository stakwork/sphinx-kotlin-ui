package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.utils.getPreferredWindowSize
import com.multiplatform.webview.web.*
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun WebAppUI(dashboardViewModel: DashboardViewModel) {
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
                        MaterialTheme {
                            val webViewState = rememberWebViewState("https://second-brain.sphinx.chat")
                            Column(Modifier.fillMaxSize()) {
                                WebView(
                                    state = webViewState,
                                    modifier = Modifier.fillMaxSize(),
                                    navigator = dashboardViewModel.customWebViewNavigator
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}