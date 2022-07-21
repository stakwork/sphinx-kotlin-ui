package chat.sphinx.common.components.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.AwtWindow
import okio.Path
import okio.Path.Companion.toOkioPath
import java.awt.FileDialog
import java.io.File

enum class FilePickerMode{
    LOAD_FILE,
    SAVE_FILE
}
@Composable
fun FilePickerDialog(
    window: ComposeWindow,
    title: String,
    filePickerMode: FilePickerMode,
    onResult: (result: Path?) -> Unit,
    desiredFileName: String? = null,
) = AwtWindow(
    create = {
        object : FileDialog(
            window,
            "Choose a file",
            when(filePickerMode) {
                FilePickerMode.LOAD_FILE -> LOAD
                FilePickerMode.SAVE_FILE -> SAVE
            }
        ) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (file != null) {
                        onResult(File(directory).resolve(file).toOkioPath())
                    } else {
                        onResult(null)
                    }
                }
            }
        }.apply {
            this.title = title
            if (desiredFileName != null) {
                this.file = desiredFileName
            }
        }
    },
    dispose = FileDialog::dispose
)
