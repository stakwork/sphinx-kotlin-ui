package chat.sphinx.common.viewmodel

import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class PinAuthenticationViewModel: PINHandlingViewModel() {

    override fun onPINTextChanged(text: String) {
        setPINState {
            copy(
                sphinxPIN = text,
                errorMessage = null,
                loading = false,
            )
        }
        if (text.length == 6) {
            onSubmitPIN()
        }
    }

    override fun onSubmitPIN() {
        val text = pinState.sphinxPIN.toCharArray()
        val userInput = authenticationCoreManager.getNewUserInput()

        pinState.sphinxPIN.forEach {
            userInput.addCharacter(it)
        }

        setPINState {
            copy(
                success = false,
                errorMessage = null,
                infoMessage = null,
                loading = true,
            )
        }

        val request = AuthenticationRequest.LogIn(privateKey = null)

        scope.launch(dispatchers.default) {
            authenticationCoreManager.authenticate(
                userInput,
                listOf(request)
            ).collect { response ->
                when (response) {
                    is AuthenticateFlowResponse.Success -> {
                        setPINState {
                            copy(
                                infoMessage = "Valid PIN",
                                errorMessage = null,
                                loading = false,
                                success = true
                            )
                        }

                        onAuthenticationSucceed()

                    }
                    is AuthenticateFlowResponse.Error -> {
                        setPINState {
                            copy(
                                infoMessage = null,
                                errorMessage = "Invalid PIN",
                                loading = false,
                                success = false
                            )
                        }

                    }
                    is AuthenticateFlowResponse.WrongPin -> {
                        setPINState {
                            copy(
                                infoMessage = null,
                                errorMessage = "Invalid PIN",
                                loading = false,
                                success = false
                            )
                        }
                    }
                    else -> { }
                }
            }
        }
    }
    abstract fun onAuthenticationSucceed()
}