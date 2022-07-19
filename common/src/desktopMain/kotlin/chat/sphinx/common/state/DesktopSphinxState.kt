package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.DialogState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.platform.getFileSystem
import chat.sphinx.utils.platform.getSphinxDirectory
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.dashboard.ChatId
import io.matthewnelson.kmp.tor.manager.common.event.TorManagerEvent
import java.awt.image.BufferedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import okio.Path

object ContentState {
    lateinit var windowState: WindowState
    val scope = CoroutineScope(Dispatchers.IO)

    val filePickerDialog = FilePickerDialogState<Path?>()

    fun applyContent(state: WindowState): ContentState {
        windowState = state

        val sphinxDirectory = getSphinxDirectory()
        if (!getFileSystem().exists(sphinxDirectory)) {
            getFileSystem().createDirectories(sphinxDirectory)
        }
        isContentReady.value = false

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

    fun onContentReady(screenType: ScreenType) {
        AppState.screenState(screenType)
        isContentReady.value = true
        isAppReady.value = true
    }
}