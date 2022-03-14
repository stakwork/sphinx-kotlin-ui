package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.dashboard.ChatId
import java.awt.image.BufferedImage
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.jetbrains.skia.Image

object ContentState {
    lateinit var windowState: WindowState
    val scope = CoroutineScope(Dispatchers.IO)

    fun applyContent(state: WindowState): ContentState {
        windowState = state

        isContentReady.value = false

        init()

        return this
    }

    private val isAppReady = mutableStateOf(false)

    fun isAppReady(): Boolean {
        return isAppReady.value
    }

    private val isContentReady = mutableStateOf(false)
    fun isContentReady(): Boolean {
        return isContentReady.value
    }

    // application content initialization
    private fun init() {
        if (isContentReady.value)
            return

        scope.launch(Dispatchers.IO) {
            delay(2000L).let {
                onContentReady(ScreenType.LandingScreen)
            }
        }

    }

    // preview/fullscreen image managing
    fun isMainImageEmpty(): Boolean {
        return MainImageWrapper.isEmpty()
    }

    fun landingScreen() {
        isContentReady.value = false
        AppState.screenState(ScreenType.LandingScreen)
    }

    fun setMainImage(chat: Chat) {
        if (MainImageWrapper.getId() == chat.id) {
            if (!isContentReady()) {
                onContentReady(ScreenType.LandingScreen)
            }
            return
        }
        isContentReady.value = false

        scope.launch(Dispatchers.IO) {
            onContentReady(ScreenType.LandingScreen)
        }
    }

    private fun onContentReady(screenType: ScreenType) {
        AppState.screenState(screenType)
        isContentReady.value = true
        isAppReady.value = true
    }
}

private object MainImageWrapper {
    var mainImageAsImageBitmap = mutableStateOf(ImageBitmap(1, 1))

    // picture adapter
    private var chat: MutableState<Chat?> = mutableStateOf(null)

    fun setChat(chat: Chat) {
        this.chat.value = chat
    }

    fun isEmpty(): Boolean {
        return chat.value == null
    }

    fun clear() {
        chat.value = null
    }

    fun getName(): String? {
        return chat.value?.name?.value
    }


    fun getId(): ChatId? {
        return chat.value?.id
    }

    private fun copy(bitmap: BufferedImage) : BufferedImage {
        var result = BufferedImage(bitmap.width, bitmap.height, bitmap.type)
        val graphics = result.createGraphics()
        graphics.drawImage(bitmap, 0, 0, result.width, result.height, null)
        return result
    }
}
