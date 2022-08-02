package chat.sphinx.common.components.browser

import androidx.compose.runtime.Composable
import java.net.URL

@Composable
actual fun SphinxWebView(url: URL) {
    WebView(url)
}

@Composable
actual fun SphinxWebView(htmlContent: String) {
    WebView(htmlContent)
}