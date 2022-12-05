package chat.sphinx.webview

import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter
import org.cef.CefApp
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefMessageRouter
import org.cef.handler.CefFocusHandlerAdapter
import java.awt.BorderLayout
import java.awt.Component
import java.awt.KeyboardFocusManager
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import kotlin.system.exitProcess

class CefBrowser(
    url:String,
    useOSR: Boolean,
    isTransparent: Boolean
): JFrame() {
    private var browserFocus = true
    private val cefApp: CefApp
    private val client: CefClient
    private val browser: CefBrowser
    private val browserUi: Component

    init {
        val builder = CefAppBuilder()
        builder.cefSettings.windowless_rendering_enabled = useOSR

        builder.setAppHandler(object: MavenCefAppHandlerAdapter() {
            override fun stateHasChanged(state: CefApp.CefAppState?) {
                if (state == CefApp.CefAppState.TERMINATED) {
                    exitProcess(0)
                }
            }
        })

        cefApp = builder.build()
        client = cefApp.createClient()
        val msgRouter = CefMessageRouter.create()
        client.addMessageRouter(msgRouter)

        browser = client.createBrowser(url, useOSR, isTransparent)
        browserUi = browser.uiComponent


        client.addFocusHandler(object : CefFocusHandlerAdapter() {
            override fun onGotFocus(browser: CefBrowser?) {
                if (browserFocus) return

                browserFocus = true
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner()
                browser?.setFocus(true)
            }

            override fun onTakeFocus(browser: CefBrowser?, next: Boolean) {
                browserFocus = false
            }
        })

        contentPane.add(browserUi, BorderLayout.CENTER)
        pack()
        setSize(1080, 720)
        isVisible = true

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                CefApp.getInstance().dispose()
                dispose()
            }
        })
    }

    companion object {
        private const val SECOND_BRAIN = "https://second-brain.sphinx.chat"
        private const val GOOGLE = "https://www.google.com"
        private const val MY_GITHUB = "https://github.com/MathRoda"
    }
}