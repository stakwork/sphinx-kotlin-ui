package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.soywiz.korio.serialization.xml.Xml.Companion.Text

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

            MaterialTheme {
                val webViewState = rememberWebViewStateWithHTMLData(
                    "<body><div background:red;></div></body>",
                    null,
                    "utf-8",
                    "text/html",
                    null
                )

                Column(Modifier.fillMaxSize()) {
                    val text = webViewState.let {
                        "${it.pageTitle ?: ""} ${it.loadingState} ${it.lastLoadedUrl ?: ""}"
                    }
                    Text(text)
                    WebView(
                        state = webViewState,
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }

        }
    }
}