package chat.sphinx.common.state

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.WindowState
import chat.sphinx.utils.platform.getFileSystem
import chat.sphinx.utils.platform.getSphinxDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import okio.Path

object ContentState {
    lateinit var windowState: WindowState
    val scope = CoroutineScope(Dispatchers.IO)

    val sendFilePickerDialog = FilePickerDialogState<Path?>()
    val saveFilePickerDialog = FilePickerDialogState<Path?>()


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