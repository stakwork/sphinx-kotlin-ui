import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import chat.sphinx.common.components.Dashboard
import chat.sphinx.common.SplashScreen

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            Dashboard()
        }
    }
}