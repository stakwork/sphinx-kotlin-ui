package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class PinType {
    Success,
    Loading,
    Error

}

object BackUpPinState {
    private var backUpPin: MutableState<PinType> = mutableStateOf(PinType.Loading)

    fun pinState() : PinType {
        return backUpPin.value
    }

    fun pinState(state: PinType) {
        backUpPin.value = state
    }
}