package chat.sphinx.common.store

import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.concepts.authentication.coordinator.AuthenticationResponse
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.di.container.SphinxContainer
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DashboardStore: PINHandlingViewModel() {

    override fun onPINTextChanged(text: String) {
        setPINState {
            copy(
                sphinxPIN = text,
                errorMessage = null
            )
        }
    }

    override fun onSubmitPIN() {
        val privateKey = Password(pinState.sphinxPIN.toCharArray())
        val request = AuthenticationRequest.LogIn(privateKey)

        setPINState {
            copy(
                infoMessage = "Entering PIN"
            )
        }
        scope.launch(SphinxContainer.appModule.dispatchers.default) {
            SphinxContainer.authenticationModule.authenticationCoreManager.authenticate(
                privateKey,
                request
            ).firstOrNull().let { response ->

                if (response is AuthenticationResponse.Success.Key) {
                    setPINState {
                        copy(
                            infoMessage = "Valid PIN",
                            errorMessage = null
                        )
                    }
                    // Update our persisted string value with new
                    // login time.
//                if (updateLastLoginTimeOnSuccess) {
//                    updateSettingsImpl(
//                        timeoutSettingHours.toInt(),
//                        response.encryptionKey
//                    )
//                }
                    response.encryptionKey
                    val ek = SphinxContainer.authenticationModule.authenticationCoreManager.getEncryptionKey()
                    if (ek == response.encryptionKey) {

                    }
                } else {
                    setPINState {
                        copy(
                            errorMessage = "Invalid PIN",
                            infoMessage = null
                        )
                    }
                    // Error validating the private key stored here
                    // to login with, so clear it to require user
                    // authentication
//                updateSettingsImpl(
//                    timeoutSettingHours.toInt(),
//                    null
//                )
//                    null
                }
            }
        }

    }

}