package chat.sphinx.common

import chat.sphinx.components.browser.SphinxFeedUrlViewer
import chat.sphinx.wrapper.chat.TribeData
import java.net.URL

actual fun handleSphinxTribeData(tribeName: String, tribeData: TribeData) {
    tribeData.appUrl?.let { appUrl ->
        SphinxFeedUrlViewer.tribeAppUrl.value = Pair(
            tribeName,
            URL(appUrl.value)
        )
    }
}