package chat.sphinx.common.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.AppState
import chat.sphinx.common.state.NewUserState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.common.state.SphinxState

class NewUserStore {

    var state: NewUserState by mutableStateOf(initialState())
        private set

    fun onInvitationCodeTextChanged(text: String) {
        setState { copy(invitationCodeText = text) }
    }

    fun onSubmitInvitationCode() {
        // TODO: Check if pin is correct
        if (state.invitationCodeText == "212121") {
            // Correct Pin
            setState {
                copy(invitationCodeText = "")
            }
            AppState.screenState(ScreenType.DashboardScreen)
        } else {
            setState {
                copy(errorMessage = "Invitation code incorrect")
            }
        }
    }

    private fun initialState(): NewUserState = NewUserState()

    private inline fun setState(update: NewUserState.() -> NewUserState) {
        state = state.update()
    }
}