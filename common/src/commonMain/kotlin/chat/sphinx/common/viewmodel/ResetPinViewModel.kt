package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ResetPinState
import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.concepts.authentication.core.model.UserInput
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect


class ResetPinViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val authenticationCoreManager = SphinxContainer.authenticationModule.authenticationCoreManager
    private val request = AuthenticationRequest.ResetPassword()

    var resetPinState: ResetPinState by mutableStateOf(ResetPinState())

    private inline fun setResetNewPinState(update: ResetPinState.() -> ResetPinState) {
        resetPinState = resetPinState.update()
    }

    private fun newUserPintoUserInput(): UserInput {
        val newUserPinInput = authenticationCoreManager.getNewUserInput()
        resetPinState.confirmedPin.forEach {
            newUserPinInput.addCharacter(it)
        }
        return newUserPinInput
    }

    fun onCurrentPinChanged(pin: String) {
        setResetNewPinState {
            copy(
                currentPin = pin,
                status = null,
                errorMessage = null
            )
        }
        if (pin.length == 6) {
            onSubmitPIN()
        }
        checkValidInput()
    }

    fun onNewPinChanged(pin: String) {
        setResetNewPinState {
            copy(
                newPin = pin
            )
        }
        checkValidInput()
    }

    fun onConfirmedNewPinChanged(pin: String) {
        setResetNewPinState {
            copy(
                confirmedPin = pin
            )
        }
        checkValidInput()
    }


    private fun onSubmitPIN() {
        val userInput = authenticationCoreManager.getNewUserInput()
        resetPinState.currentPin.forEach {
            userInput.addCharacter(it)
        }

        scope.launch(dispatchers.default) {
            authenticationCoreManager.authenticate(
                userInput,
                listOf(request)
            ).collect { response ->

                when (response) {
                    is AuthenticateFlowResponse.PasswordConfirmedForReset -> {
                        setResetNewPinState {
                            copy(
                                status = response,
                                errorMessage = null,
                                buttonEnabled = false
                            )
                        }
                        checkValidInput()
                    }
                    else -> {
                        setResetNewPinState {
                            copy(
                                status = response,
                                errorMessage = "Invalid Pin",
                                buttonEnabled = false
                            )
                        }
                        checkValidInput()
                    }
                }
            }
        }
    }

    fun resetPassword() {
        scope.launch(dispatchers.mainImmediate) {
            (resetPinState.status as? AuthenticateFlowResponse.PasswordConfirmedForReset)?.let { response ->
                response.storeNewPasswordToBeSet(newUserPintoUserInput())
                authenticationCoreManager.resetPassword(
                    response,
                    newUserPintoUserInput(),
                    listOf(request)
                ).collect { resetPasswordResponse ->
                    setResetNewPinState {
                        copy(
                            status = resetPasswordResponse
                        )
                    }
                }
            }
        }
    }

    private fun checkValidInput() {
        resetPinState.apply {
            if (status is AuthenticateFlowResponse.PasswordConfirmedForReset) {
                if (currentPin.length == 6 && newPin.length == 6 && confirmedPin.length == 6) {
                    if (newPin == confirmedPin) {
                        setResetNewPinState {
                            copy(
                                errorMessage = null,
                                buttonEnabled = true
                            )
                        }
                        return
                    }
                }
            }

            setResetNewPinState {
                copy(
                    buttonEnabled = false
                )
            }
        }
    }
}

