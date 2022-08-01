package chat.sphinx.utils

import chat.sphinx.common.state.ContentState
import chat.sphinx.utils.platform.getFileSystem
import chat.sphinx.wrapper.message.media.FileName
import okio.Path

actual suspend fun saveFile(fileName: FileName, path: Path) {
    val saveFilepath = ContentState.saveFilePickerDialog.awaitResult(
        desiredFileName = fileName.value
    )
    if (saveFilepath != null) {
        getFileSystem().copy(
            path,
            saveFilepath
        )
    }
}