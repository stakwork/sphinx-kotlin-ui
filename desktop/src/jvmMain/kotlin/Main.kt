
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import dev.datlag.kcef.KCEF
import dev.datlag.tooling.Tooling
import dev.datlag.tooling.getApplicationWriteableRootFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import theme.LocalSpacing
import theme.Spacing
import java.io.File

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
                var error by remember { mutableStateOf("") }
                var downloading by remember { mutableStateOf(0F) }
                var initialized by remember { mutableStateOf(false) } // if true, KCEF can be used to create clients, browsers etc

                val appWriteableRootFolder = Tooling.getApplicationWriteableRootFolder("Sphinx") ?: File("./")
                val kcefInstallDir = File(appWriteableRootFolder, "kcef-bundle")

                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) { // IO scope recommended but not required
                        KCEF.init(
                            builder = {
                                installDir(kcefInstallDir) // recommended, but not necessary

                                progress {
                                    onDownloading {
                                        downloading = it
                                        // use this if you want to display a download progress for example
                                    }
                                    onInitialized {
                                        initialized = true
                                    }
                                }
                            },
                            onError = {
                                error = it?.localizedMessage ?: ""
                            },
                            onRestartRequired = {
                                restartRequired = true
                            }
                        )
                    }
                }

//                if (restartRequired) {
//                    toast("Restart Required")
//                } else if (error.isNotEmpty()) {
//                    toast(error)
//                } else {
//                    if (!initialized) {
//                        toast("Downloading $downloading%")
//                    }
//                }

                DisposableEffect(Unit) {
                    onDispose {
                        KCEF.disposeBlocking()
                    }
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

