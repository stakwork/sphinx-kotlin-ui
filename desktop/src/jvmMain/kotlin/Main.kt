import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.*
import chat.sphinx.common.Res
import chat.sphinx.common.components.Dashboard
import chat.sphinx.common.SplashScreen
import chat.sphinx.common.components.AuthenticatorScreen
import chat.sphinx.common.components.LandingScreen
import chat.sphinx.common.state.AppState
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.common.store.SphinxStore
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize

fun main() = application {
    val windowState = rememberWindowState()
    val content = remember {
        ContentState.applyContent(windowState)
    }
    val icon = imageResource(Res.drawable.sphinx_logo)

    val sphinxStore = remember { SphinxStore() }
    val sphinxState = sphinxStore.state

    when (AppState.screenState()) {
        ScreenType.SplashScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(500, 350)
                ),
                undecorated = true,
                icon = icon,
            ) {
                MaterialTheme {
                    SplashScreen()
                }
            }
        }
        ScreenType.DashboardScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(800, 800)
                ),
                icon = icon
            ) {
                MaterialTheme {
                    Dashboard(sphinxState)
                }
            }
        }
        ScreenType.AuthenticatorScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(800, 800)
                ),
                icon = icon
            ) {
                MaterialTheme {
                    AuthenticatorScreen(
                        text = sphinxState.pinInput,
                        errorMessage = sphinxState.errorMessage,
                        onTextChanged = sphinxStore::onTextChanged,
                        onSubmitPin = sphinxStore::onSubmitPin
                    )
                }
            }
        }
        ScreenType.LandingScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(800, 800)
                ),
                icon = icon
            ) {
                MaterialTheme {
                    LandingScreen()
                }
            }
        }
    }
}