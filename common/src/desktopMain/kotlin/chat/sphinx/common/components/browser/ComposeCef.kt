package chat.sphinx.common.components.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter
import org.cef.CefApp
import org.cef.CefApp.CefAppState
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefMessageRouter
import org.cef.handler.CefFocusHandlerAdapter
import java.awt.KeyboardFocusManager


object ComposeCef {
    private val cefApp: CefApp
    private val client: CefClient
    var browserFocus = true

    // Arguments
    val useOSR: Boolean = false
    val isTransparent: Boolean = false

    init {


        // (0) Initialize CEF using the maven loader
        val builder = CefAppBuilder()
        // windowless_rendering_enabled must be set to false if not wanted.
        // windowless_rendering_enabled must be set to false if not wanted.
        builder.cefSettings.windowless_rendering_enabled = useOSR
        // USE builder.setAppHandler INSTEAD OF CefApp.addAppHandler!
        // Fixes compatibility issues with MacOSX
        // USE builder.setAppHandler INSTEAD OF CefApp.addAppHandler!
        // Fixes compatibility issues with MacOSX
        builder.setAppHandler(object : MavenCefAppHandlerAdapter() {
            override fun stateHasChanged(state: CefAppState) {
                // Shutdown the app if the native CEF part is terminated
                if (state == CefAppState.TERMINATED) System.exit(0)
            }
        })

        // JCEF arguments can be set here...
//        builder.addJcefArgs(*args)

        // (1) The entry point to JCEF is always the class CefApp. There is only one
        //     instance per application and therefore you have to call the method
        //     "getInstance()" instead of a CTOR.
        //
        //     CefApp is responsible for the global CEF context. It loads all
        //     required native libraries, initializes CEF accordingly, starts a
        //     background task to handle CEF's message loop and takes care of
        //     shutting down CEF after disposing it.
        //
        //     WHEN WORKING WITH MAVEN: Use the builder.build() method to
        //     build the CefApp on first run and fetch the instance on all consecutive
        //     runs. This method is thread-safe and will always return a valid app
        //     instance.
        // TODO: Run this in a scope and don't block UI
        cefApp = builder.build()

        // (2) JCEF can handle one to many browser instances simultaneous. These
        //     browser instances are logically grouped together by an instance of
        //     the class CefClient. In your application you can create one to many
        //     instances of CefClient with one to many CefBrowser instances per
        //     client. To get an instance of CefClient you have to use the method
        //     "createClient()" of your CefApp instance. Calling an CTOR of
        //     CefClient is not supported.
        //
        //     CefClient is a connector to all possible events which come from the
        //     CefBrowser instances. Those events could be simple things like the
        //     change of the browser title or more complex ones like context menu
        //     events. By assigning handlers to CefClient you can control the
        //     behavior of the browser. See tests.detailed.MainFrame for an example
        //     of how to use these handlers.
        client = cefApp.createClient()

        // (3) Create a simple message router to receive messages from CEF.
        val msgRouter = CefMessageRouter.create()
        client.addMessageRouter(msgRouter)


        // (7) To take care of shutting down CEF accordingly, it's important to call
        //     the method "dispose()" of the CefApp instance if the Java
        //     application will be closed. Otherwise you'll get asserts from CEF.
//        window.addWindowListener(object : WindowAdapter() {
//            override fun windowClosing(e: WindowEvent) {
//                CefApp.getInstance().dispose()
//                window.dispose()
//            }
//        })
    }

    @Composable
    fun rememberCEFBrowser(
        startURL: String
    ) = remember {
        cefBrowser(startURL)
    }

    fun cefBrowser(
        startURL: String
    ): CefBrowser {

        // (4) One CefBrowser instance is responsible to control what you'll see on
        //     the UI component of the instance. It can be displayed off-screen
        //     rendered or windowed rendered. To get an instance of CefBrowser you
        //     have to call the method "createBrowser()" of your CefClient
        //     instances.
        //
        //     CefBrowser has methods like "goBack()", "goForward()", "loadURL()",
        //     and many more which are used to control the behavior of the displayed
        //     content. The UI is held within a UI-Compontent which can be accessed
        //     by calling the method "getUIComponent()" on the instance of CefBrowser.
        //     The UI component is inherited from a java.awt.Component and therefore
        //     it can be embedded into any AWT UI.
        val browser = client.createBrowser(startURL, useOSR, isTransparent)

        // Clear focus from the address field when the browser gains focus.
        client.addFocusHandler(object : CefFocusHandlerAdapter() {
            override fun onGotFocus(browser: CefBrowser) {
                if (browserFocus) return
                browserFocus = true
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner()
                browser.setFocus(true)
            }

            override fun onTakeFocus(browser: CefBrowser, next: Boolean) {
                browserFocus = false
            }
        })

        return browser
    }
}