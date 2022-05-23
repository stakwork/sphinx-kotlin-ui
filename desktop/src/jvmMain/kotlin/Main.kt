import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.*
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.Res
import chat.sphinx.common.components.Dashboard
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.LandingScreen
import chat.sphinx.common.state.AppState
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.common.viewmodel.SphinxStore
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val windowState = rememberWindowState()
    val content = remember {
        ContentState.applyContent(windowState)
    }
    val icon = imageResource(DesktopResource.drawable.sphinx_icon)

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
                    SphinxSplash()
                }
            }
        }
        ScreenType.DashboardScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(950, 600)
                ),

                icon = icon
            ) {
                MenuBar {
                    Menu("Sphinx") {
                        Item("About", icon = icon, onClick = { })
                        Item("Remove Account from this machine", onClick = {
                            sphinxStore.removeAccount()
                        })
                        Item("Exit", onClick = ::exitApplication)
                    }
                }
                MaterialTheme {
                    Dashboard(sphinxState)
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
                MenuBar {
                    Menu("Sphinx") {
                        Item("About", icon = icon, onClick = { })
                        Item("Exit", onClick = ::exitApplication, shortcut = KeyShortcut(Key.Escape))
                    }
                }
                MaterialTheme {
                    LandingScreen()
                }
            }
        }
    }
}