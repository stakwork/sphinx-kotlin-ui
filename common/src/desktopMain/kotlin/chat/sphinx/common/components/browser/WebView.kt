package chat.sphinx.common.components.browser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import com.benasher44.uuid.uuid4
import okio.FileSystem
import java.net.URL


@Composable
fun WebView(
    startURL: URL
) {
    val cefBrowser = ComposeCef.rememberCEFBrowser(startURL.toString())

    SwingPanel(
        background = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                cefBrowser.uiComponent.setSize(
                    layoutCoordinates.size.width,
                    layoutCoordinates.size.height
                )
            },
        factory = {
            cefBrowser.uiComponent
        }
    )
}

@Composable
fun WebView(
    htmlContent: String
) {
    key(htmlContent) {
        val randomFileName = uuid4().toString()
        val temporaryFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve(randomFileName).toFile()

        temporaryFile.writeText(
            htmlContent
        )

        WebView(
            URL("file://${temporaryFile.absolutePath}")
        )
    }
}