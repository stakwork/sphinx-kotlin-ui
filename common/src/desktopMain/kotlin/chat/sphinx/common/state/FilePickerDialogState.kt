package chat.sphinx.common.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred

class FilePickerDialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    val isAwaiting get() = onResult != null
    var desiredFileName: String? = null

    suspend fun awaitResult(desiredFilename: String? = null): T {
        this.desiredFileName = desiredFilename
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        this.desiredFileName = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}