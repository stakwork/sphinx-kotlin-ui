package chat.sphinx.components.browser

import androidx.compose.runtime.mutableStateOf
import java.net.URL

object SphinxFeedUrlViewer {
    val tribeAppUrl = mutableStateOf<Pair<String, URL>?>(null)

    // TODO: Use a getter and a setter... to better control what happens when we change tribes...
}