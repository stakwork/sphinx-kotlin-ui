package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardState
import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LockedDashboardViewModel: PINHandlingViewModel() {

    override fun onPINTextChanged(text: String) {
        setPINState {
            copy(
                sphinxPIN = text,
                errorMessage = null
            )
        }
    }

    override fun onSubmitPIN() {
        val password = Password(pinState.sphinxPIN.toCharArray())
        val authenticationCoreManager = SphinxContainer.authenticationModule.authenticationCoreManager
        val userInput = authenticationCoreManager.getNewUserInput()
        pinState.sphinxPIN.forEach {
            userInput.addCharacter(it)
        }
        val request = AuthenticationRequest.LogIn(password)

        scope.launch(SphinxContainer.appModule.dispatchers.default) {
            SphinxContainer.authenticationModule.authenticationCoreManager.authenticate(
                userInput,
                listOf(request)
            ).collect { response ->
                when (response) {
                    is AuthenticateFlowResponse.Success -> {
                        setPINState {
                            copy(
                                infoMessage = "Valid PIN",
                                errorMessage = null
                            )
                        }

                        DashboardState.screenState(DashboardScreenType.Unlocked)
                    }
                    is AuthenticateFlowResponse.Error -> {
                        setPINState {
                            copy(
                                infoMessage = "Invalid PIN",
                                errorMessage = null
                            )
                        }
                    }
                    is AuthenticateFlowResponse.WrongPin -> {
                        setPINState {
                            copy(
                                infoMessage = "Invalid PIN",
                                errorMessage = null
                            )
                        }
                    }
                    else -> { }
                }
            }
        }

    }
}