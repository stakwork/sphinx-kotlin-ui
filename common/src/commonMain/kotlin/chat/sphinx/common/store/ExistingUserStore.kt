package chat.sphinx.common.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.*

class ExistingUserStore {

    var state: ExistingUserState by mutableStateOf(initialState())
        private set

    fun onKeysTextChanged(text: String) {
        setState { copy(sphinxKeys = text) }
    }

    fun onSubmitKeys() {
        // TODO: Check if pin is correct
        if (state.sphinxKeys == "212121") {
            // Correct Pin
            setState {
                copy(sphinxKeys = "")
            }
            AppState.screenState(ScreenType.DashboardScreen)
        } else {
            setState {
                copy(errorMessage = "Error connecting keys")
            }
        }
    }

    private fun initialState(): ExistingUserState = ExistingUserState()

    private inline fun setState(update: ExistingUserState.() -> ExistingUserState) {
        state = state.update()
    }
}