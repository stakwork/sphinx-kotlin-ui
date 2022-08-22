package chat.sphinx.components.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import com.sun.javafx.application.PlatformImpl
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import netscape.javascript.JSObject
import java.awt.BorderLayout
import javax.swing.JPanel

public class SphinxHandler {
    public fun handle(message: String) {
        println("message received through bridge $message")
    }
}

public val sphinxHandler: SphinxHandler = SphinxHandler()

@Composable
fun WebAppBrowserWindow(
    initialScript: String,
    windowSize: DpSize,
    onReceive: (String, HandleScript) -> Unit,
    onCloseRequest: (() -> Unit)? = null,
) {
    val openAuthorizeDialog = remember { mutableStateOf(false) }
    key(SphinxFeedUrlViewer.tribeAppUrl.value) {
        SphinxFeedUrlViewer.tribeAppUrl.value?.let { tribeFeedUrlPair ->
            // Required to make sure the JavaFx event loop doesn't finish (can happen when java fx panels in app are shown/hidden)
            val finishListener = object : PlatformImpl.FinishListener {
                override fun idle(implicitExit: Boolean) {}
                override fun exitCalled() {}
            }
            PlatformImpl.addListener(finishListener)

            Window(
                title = tribeFeedUrlPair.first,
                resizable = false,
                state = WindowState(
                    placement = WindowPlacement.Floating,
                    size = windowSize
                ),
                onCloseRequest = {
                    PlatformImpl.removeListener(finishListener)
                    onCloseRequest?.invoke()
                    SphinxFeedUrlViewer.tribeAppUrl.value = null
                },
                content = {
                    val jfxPanel = remember { JFXPanel() }
                    var jsObject = remember<JSObject?> { null }
                    var engine = remember { mutableStateOf<WebEngine?>(null) }
                    if (openAuthorizeDialog.value.not())
                        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

                            ComposeJFXPanel(
                                composeWindow = window,
                                jfxPanel = jfxPanel,
                                onCreate = {
                                    Platform.runLater {
                                        val root = WebView()
                                        engine.value = root.engine
//                                    engine.userAgent = "Sphinx"
                                        val scene = Scene(root)
                                        engine.value?.let { engine ->
                                            engine.loadWorker.stateProperty().addListener { _, _, newState ->
                                                if (newState === Worker.State.SUCCEEDED) {
                                                    //Using bridge here
//                                                    val win = root.engine.executeScript("window") as JSObject
//                                                    win.setMember("sphinxApp", sphinxHandler)
//                                                    engine.executeScript("window.addEventListener('message', (event) => {sphinxApp.handle(JSON.stringify(event.data))})")

                                                    val callback = object : HandleScript {
                                                        override fun runScript(script: String) {
                                                            println("script to run is $script")
                                                            engine.executeScript(script)
                                                        }
                                                    }
                                                    engine.executeScript(initialScript)
                                                    engine.onAlert = EventHandler { ev ->
                                                        onReceive(ev.data, callback)
                                                    }
                                                }
                                            }
                                            engine.loadWorker.exceptionProperty().addListener { _, _, newError ->
                                                println("page load error : $newError")
                                            }
                                            jfxPanel.scene = scene
                                            engine.load("https://temp-sphinx-rahul.web.app/") // can be a html document from resources ..
                                            engine.setOnError { error -> println("onError : $error") }
                                        }
                                    }
                                }, onDestroy = {
                                    Platform.runLater {
                                        jsObject?.let { jsObj ->
                                            // clean up code for more complex implementations i.e. removing javascript callbacks etc..
                                            jsObj.removeMember("java")
                                        }
                                    }
                                })
                        }
                })
        }
    }
}

interface HandleScript {
    fun runScript(query: String)
}

@Composable
fun ComposeJFXPanel(
    composeWindow: ComposeWindow,
    jfxPanel: JFXPanel,
    onCreate: () -> Unit,
    onDestroy: () -> Unit = {}
) {
    val jPanel = remember { JPanel() }
    val density = LocalDensity.current.density
    Layout(
        content = {},
        modifier = Modifier.onGloballyPositioned { childCoordinates ->
            val coordinates = childCoordinates.parentCoordinates!!
            val location = coordinates.localToWindow(Offset.Zero).round()
            val size = coordinates.size
            jPanel.setBounds(
                (location.x / density).toInt(),
                (location.y / density).toInt(),
                (size.width / density).toInt(),
                (size.height / density).toInt()
            )
            jPanel.validate()
            jPanel.repaint()
        },
        measurePolicy = { _, _ -> layout(0, 0) {} })
    DisposableEffect(jPanel) {
        composeWindow.add(jPanel)
        jPanel.layout = BorderLayout(0, 0)
        jPanel.add(jfxPanel)
        onCreate()
        onDispose {
            onDestroy()
            composeWindow.remove(jPanel)
        }
    }
}