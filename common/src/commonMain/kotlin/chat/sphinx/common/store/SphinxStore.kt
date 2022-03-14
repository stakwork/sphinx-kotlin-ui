package chat.sphinx.common.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.AppState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.common.state.SphinxState

class SphinxStore {

    var state: SphinxState by mutableStateOf(initialState())
        private set

    fun onTextChanged(text: String) {
        setState { copy(pinInput = text) }
    }

    fun onSubmitPin() {
        // TODO: Check if pin is correct
        if (state.pinInput == "212121") {
            // Correct Pin
            setState {
                copy(pinInput = "")
            }
            AppState.screenState(ScreenType.DashboardScreen)
        } else {
            setState {
                copy(errorMessage = "PIN incorrect")
            }
        }
    }

    private fun initialState(): SphinxState =
        SphinxState()

    private inline fun setState(update: SphinxState.() -> SphinxState) {
        state = state.update()
    }
}