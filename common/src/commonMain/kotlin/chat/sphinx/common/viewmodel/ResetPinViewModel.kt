package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ContactState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ResetPinViewModel : PinAuthenticationViewModel() {

    data class NewPinState(
        val newPin: String = "",
        val confirmedPin:String = ""
    )

    var newPinState: NewPinState by mutableStateOf(NewPinState())

    private inline fun setNewPinState(update: NewPinState.() -> NewPinState) {
        newPinState = newPinState.update()
    }

    fun setNewPin(pin: String){
        setNewPinState {
            copy(
                newPin = pin
            )
        }
    }
    fun setConfirmedNewPin(pin: String){
        setNewPinState {
            copy(
                confirmedPin = pin
            )
        }
    }

    override fun onAuthenticationSucceed() {

    }
}

