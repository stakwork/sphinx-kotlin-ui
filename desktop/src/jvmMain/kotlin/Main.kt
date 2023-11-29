import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.*
import chat.sphinx.authentication.model.OnBoardStepHandler
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.Dashboard
import chat.sphinx.common.components.LandingScreen
import chat.sphinx.common.components.chat.FilePickerDialog
import chat.sphinx.common.components.chat.FilePickerMode
import chat.sphinx.common.components.notifications.DesktopSphinxConfirmAlert
import chat.sphinx.common.components.notifications.DesktopSphinxNotifications
import chat.sphinx.common.components.notifications.DesktopSphinxToast
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.SphinxStore
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.AppTheme
import com.multiplatform.webview.web.Cef
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import theme.LocalSpacing
import theme.Spacing
import java.io.File
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val windowState = rememberWindowState()
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)

    val onBoardStepHandler = remember { OnBoardStepHandler() }
    val sphinxStore = remember { SphinxStore() }
    var currentWindow: MutableState<ComposeWindow?> = remember { mutableStateOf(null) }

    when (AppState.screenState()) {
        ScreenType.SplashScreen -> {
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
                    SphinxSplash()

                    LaunchedEffect(windowState) {
                        delay(1000L)

                        if (SphinxContainer.authenticationModule.authenticationStorage.hasCredential()) {
                            if (!onBoardStepHandler.isSignupInProgress()) {
                                ContentState.onContentReady(ScreenType.DashboardScreen)
                                return@LaunchedEffect
                            }
                        }

                        sphinxStore.restoreSignupStep()
                        ContentState.onContentReady(ScreenType.LandingScreen)
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
                currentWindow.value = window

                val dashboardViewModel = remember { DashboardViewModel() }
                this.window.addWindowFocusListener(dashboardViewModel)

                MenuBar {
                    Menu("Sphinx") {
                        Item("About Sphinx", icon = sphinxIcon, onClick = {
                            dashboardViewModel.toggleAboutSphinxWindow(true)
                        })
                        when (DashboardScreenState.screenState()) {
                            DashboardScreenType.Unlocked -> {
                                Item("Profile", onClick = {
                                    dashboardViewModel.toggleProfileWindow(true)}
                                )
                                Item("Transactions", onClick = {
                                    dashboardViewModel.toggleTransactionsWindow(true)}
                                )
                                Item("Second Brain", onClick = {
                                    dashboardViewModel.toggleWebAppWindow(true)}
                                )
                                Item("Create Tribe", onClick = {
                                    dashboardViewModel.toggleCreateTribeWindow(true, null)}
                                )

                                Item("Remove Account from this machine", onClick = {
                                    sphinxStore.removeAccount()
                                    // TODO: Hack as logic to recreate database in the same process needs to be reworked...
                                    exitApplication()
                                })
                            }
                            else -> {}
                        }
                        Item("Exit", onClick = ::exitApplication)
                    }
                }

                AppTheme(useDarkTheme = true) {
                    Dashboard(dashboardViewModel)

                    DesktopSphinxToast("Sphinx")
                    DesktopSphinxConfirmAlert("Sphinx")

                    DesktopSphinxNotifications(
                        window,
                        icon = sphinxIcon
                    )
                }

                // Init WebView

                var restartRequired by remember { mutableStateOf(false) }
                var downloading by remember { mutableStateOf(0F) }
                var initialized by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        Cef.init(builder = {
                            installDir = File("jcef-bundle")
                            settings {
                                cachePath = File("cache").absolutePath
                            }
                        }, initProgress = {
                            onDownloading {
                                downloading = max(it, 0F)
                            }
                            onInitialized {
                                initialized = true
                            }
                        }, onError = {
                            it.printStackTrace()
                        }, onRestartRequired = {
                            restartRequired = true
                        })
                    }
                }

                if (restartRequired) {
                    Text(text = "Restart required.")
                } else {
                    if (initialized) {
                    } else {
                        Text(text = "Downloading $downloading%")
                    }
                }

//                DisposableEffect(Unit) {
//                    onDispose {
//                        Cef.dispose()
//                    }
//                }
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
                currentWindow.value = window

                MenuBar {
                    Menu("Sphinx") {
                        Item("About", icon = sphinxIcon, onClick = { })
                        Item("Exit", onClick = ::exitApplication, shortcut = KeyShortcut(Key.Escape))

                        if (LandingScreenState.isUnlockedSignup()) {
                            Item("Remove Account from this machine", onClick = {
                                sphinxStore.removeAccount()
                                // TODO: Hack as logic to recreate database in the same process needs to be reworked...
                                exitApplication()
                            })
                        }
                    }
                }
                AppTheme(useDarkTheme = true) {
                    LandingScreen()
                    DesktopSphinxToast("Sphinx")
                }
            }
        }
    }
    currentWindow.value?.let { window ->
        CompositionLocalProvider(LocalSpacing provides Spacing()){
            if (ContentState.sendFilePickerDialog.isAwaiting) {
                FilePickerDialog(
                    window,
                    "Pick File",
                    FilePickerMode.LOAD_FILE,
                    onResult = {
                        ContentState.sendFilePickerDialog.onResult(it)
                    }
                )
            }
            if (ContentState.saveFilePickerDialog.isAwaiting) {
                FilePickerDialog(
                    window,
                    "Save File",
                    FilePickerMode.SAVE_FILE,
                    onResult = {
                        ContentState.saveFilePickerDialog.onResult(it)
                    },
                    desiredFileName = ContentState.saveFilePickerDialog.desiredFileName
                )
            }
        }
    }



//    Window(
//        onCloseRequest = ::exitApplication,
//        title = "Video Player",
//        state = WindowState(
//            position = WindowPosition.Aligned(Alignment.Center),
//            size = getPreferredWindowSize(600, 600)
//        ),
//        icon = sphinxIcon
//    ) {
//        AppTheme(useDarkTheme = true) {
//            val videoPlayerState = rememberVideoPlayerState(
//                time = 0L,
//                isPlaying = false,
//            )
//            val time by videoPlayerState.time.collectAsState()
//            val isPlaying by videoPlayerState.isPlaying.collectAsState()
//            val length by videoPlayerState.length.collectAsState()
//
//            VideoPlayer(
//                mrl = "/Users/tomastiminskas/Desktop/end_video_2.mp4",
//                state = videoPlayerState,
//            )
//        }
//    }
}

