package chat.sphinx.common.components.browser

import androidx.compose.runtime.Composable
import java.net.URL

@Composable
expect fun SphinxWebView(
    url: URL
)

@Composable
expect fun SphinxWebView(
    htmlContent: String
)