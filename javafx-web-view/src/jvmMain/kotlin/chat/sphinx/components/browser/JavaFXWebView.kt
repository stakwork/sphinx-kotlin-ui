package chat.sphinx.components.browser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView;
import kotlinx.coroutines.launch
import java.net.URL


@Composable
fun JavaFXWebView(
    startURL: URL
) {
    val jfxPanel = remember {
        JFXPanel()
    }

    LaunchedEffect(startURL) {
        Platform.runLater {
            val webView = WebView()
            webView.engine.load(startURL.toString())
            val scene = Scene(webView)
            jfxPanel.scene = scene
        }
    }

    SwingPanel(
        background = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->

            },
        factory = {
            jfxPanel
        }
    )
}

@Composable
fun JavaFXWebView(
    htmlContent: String
) {
    val jfxPanel = remember {
        JFXPanel()
    }

    LaunchedEffect(htmlContent) {
        Platform.runLater {
            val webView = WebView()
            webView.engine.loadContent(htmlContent)
            val scene = Scene(webView)
            jfxPanel.scene = scene
        }
    }

    SwingPanel(
        background = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->

            },
        factory = {
            jfxPanel
        }
    )
}
