package chat.sphinx.components.browser

import androidx.compose.runtime.mutableStateOf
import java.net.URL

object SphinxFeedUrlViewer {
    val tribeAppUrl = mutableStateOf<Pair<String, URL>?>(null)
}