package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.SignUpState

class SignUpViewModel {

    var signUpState: SignUpState by mutableStateOf(initialSignUpState())

    private fun initialSignUpState(): SignUpState = SignUpState()

    private inline fun setSignUpState(update: SignUpState.() -> SignUpState) {
        signUpState = signUpState.update()
    }

    fun onNicknameChanged(nickname: String) {
        setSignUpState {
            copy(
                nickname = nickname
            )
        }
        checkValidInput()
    }

    fun onNewPinChanged(newPin: String) {
        setSignUpState {
            copy(
                newPin = newPin
            )
        }
        checkValidInput()
    }

    fun onConfirmedPinChanged(confirmedPin: String) {
        setSignUpState {
            copy(
                confirmedPin = confirmedPin
            )
        }
        checkValidInput()
    }

    private fun checkValidInput() {
        signUpState.apply {
            if (nickname.isNotEmpty() && newPin.length == 6 && confirmedPin.length == 6) {
                if (newPin == confirmedPin) {
                    setSignUpState {
                        copy(
                            basicInfoButtonEnabled = true
                        )
                    }
                }
            } else {
                setSignUpState {
                    copy(
                        basicInfoButtonEnabled = false
                    )
                }
            }
        }
        return
    }
}