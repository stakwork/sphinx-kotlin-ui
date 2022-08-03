package chat.sphinx.common.components.browser

import androidx.compose.runtime.Composable
import chat.sphinx.components.browser.JavaFXWebView
import java.net.URL

@Composable
actual fun SphinxWebView(url: URL) {
    JavaFXWebView(url)
}

@Composable
actual fun SphinxWebView(htmlContent: String) {
    JavaFXWebView(htmlContent)
}