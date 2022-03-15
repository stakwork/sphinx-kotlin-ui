package chat.sphinx.common.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class NewUserStore {
    val scope = CoroutineScope(Dispatchers.IO)

    var state: NewUserState by mutableStateOf(initialState())
        private set

    fun onInvitationCodeTextChanged(text: String) {
        setState {
            copy(
                invitationCodeText = text,
                errorMessage = null
            )
        }
    }

    fun onSubmitInvitationCode() {
        // TODO: Check if pin is correct
        OnboardingState.status(OnboardingStatus.Connecting)
        if (state.invitationCodeText == "212121") {
            scope.launch(Dispatchers.IO) {
                delay(7000L).let {
                    setState {
                        copy(invitationCodeText = "")
                    }
                    OnboardingState.status(OnboardingStatus.Successful)
//                AppState.screenState(ScreenType.DashboardScreen)
                }
            }
        } else {
            setState {
                copy(errorMessage = "Invitation code incorrect")
            }
            OnboardingState.status(null)
        }
    }

    private fun initialState(): NewUserState = NewUserState()

    private inline fun setState(update: NewUserState.() -> NewUserState) {
        state = state.update()
    }
}