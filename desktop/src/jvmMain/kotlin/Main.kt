import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.*
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.Dashboard
import chat.sphinx.common.components.LandingScreen
import chat.sphinx.common.components.landing.ConnectingDialog
import chat.sphinx.common.components.profile.Profile
import chat.sphinx.common.components.notifications.DesktopSphinxNotifications
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.SphinxStore
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.AppTheme
import kotlinx.coroutines.delay
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import java.awt.event.WindowStateListener

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val windowState = rememberWindowState()
    val content = remember {
        ContentState.applyContent(windowState)
    }
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)

    val sphinxStore = remember { SphinxStore() }
    val sphinxState = sphinxStore.state

//    val rememberSphinxTray = remember {
//        DesktopSphinxNotificationManager.sphinxTrayState
//    }
//    Tray(
//        state = rememberSphinxTray,
//        icon = sphinxIcon,
//        menu = {
//            Item(
//                "Send notification",
//                onClick = {
//                    rememberSphinxTray.sendNotification(
//                        Notification("Sphinx Notification", "Message from Sphinx App!")
//                    )
//                }
//            )
//            Item(
//                "Exit",
//                onClick = ::exitApplication
//            )
//        }
//    )
    when (AppState.screenState()) {
        ScreenType.SplashScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(800, 500)
                ),
                undecorated = true,
                icon = sphinxIcon,
            ) {
                AppTheme {
                    SphinxSplash()
                    LaunchedEffect(windowState) {
                        delay(1000L)
                        if (SphinxContainer.authenticationModule.authenticationStorage.hasCredential()) {
                            ContentState.onContentReady(ScreenType.DashboardScreen)
                        } else {
                            ContentState.onContentReady(ScreenType.LandingScreen)
                        }
                    }
                }
            }
        }
        ScreenType.DashboardScreen -> {

            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(1200, 800)
                ),
                icon = sphinxIcon
            ) {
                val dashboardViewModel = remember { DashboardViewModel() }
                this.window.addWindowFocusListener(dashboardViewModel)

                MenuBar {
                    Menu("Sphinx") {
                        Item("About", icon = sphinxIcon, onClick = { })
                        when (DashboardState.screenState()) {
                            DashboardScreenType.Unlocked ->{
                                Item("Profile", onClick = {dashboardViewModel.toggleProfileWindow(true)})
                            }
                            else -> {}
                        }
                        Item("Remove Account from this machine", onClick = {
                            sphinxStore.removeAccount()
                            // TODO: Hack as logic to recreate database in the same process needs to be reworked...
                            exitApplication()
                        })
                        Item("Exit", onClick = ::exitApplication)
                    }
                }

                AppTheme(useDarkTheme = true) {
                    DesktopSphinxNotifications(
                        icon = sphinxIcon
                    )
                    Dashboard(sphinxState, dashboardViewModel)
                }
            }
        }

        ScreenType.LandingScreen -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Sphinx",
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center),
                    size = getPreferredWindowSize(1000, 800)
                ),
                icon = sphinxIcon
            ) {
                MenuBar {
                    Menu("Sphinx") {
                        Item("About", icon = sphinxIcon, onClick = { })
                        Item("Exit", onClick = ::exitApplication, shortcut = KeyShortcut(Key.Escape))
                    }
                }
                AppTheme(useDarkTheme = true) {
                    LandingScreen()
                }
            }
        }
    }
}