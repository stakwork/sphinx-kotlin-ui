package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.*
import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class PinAuthenticationViewModel: PINHandlingViewModel() {

    val authenticationCoreManager = SphinxContainer.authenticationModule.authenticationCoreManager
    val dispatchers = SphinxContainer.appModule.dispatchers


    override fun onPINTextChanged(text: String) {
        setPINState {
            copy(
                sphinxPIN = text,
                errorMessage = null,
                loading = false,
            )
        }
        if (text.length==6) {
            onSubmitPIN()
        }
    }

    override fun onSubmitPIN() {
        val text = pinState.sphinxPIN.toCharArray()
        val password = Password(text)
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

        val request = AuthenticationRequest.LogIn(password)

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
                                infoMessage = "Invalid PIN",
                                errorMessage = null,
                                loading = false,
                                success = false
                            )
                        }

                    }
                    is AuthenticateFlowResponse.WrongPin -> {
                        setPINState {
                            copy(
                                infoMessage = "Invalid PIN",
                                errorMessage = null,
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