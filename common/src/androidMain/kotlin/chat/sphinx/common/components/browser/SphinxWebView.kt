package chat.sphinx.common.components.browser

import androidx.compose.runtime.Composable
import java.net.URL

@Composable
actual fun SphinxWebView(url: URL) {
    // TODO: Use good old WebView here...
}

@Composable
actual fun SphinxWebView(htmlContent: String) {
    // TODO: Use good old WebView
}