package chat.sphinx.common.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.authentication.KeyRestoreResponse
import chat.sphinx.authentication.model.RedemptionCode
import chat.sphinx.common.state.*
import chat.sphinx.di.container.SphinxContainer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExistingUserStore: PINHandlingViewModel() {

    val keyRestore = SphinxContainer.authenticationModule.keyRestore(
        SphinxContainer.networkModule.relayDataHandlerImpl
    )

    var state: ExistingUserState by mutableStateOf(initialState())
        private set

    fun onKeysTextChanged(text: String) {
        setState {
            copy(
                sphinxKeys = text,
                errorMessage = null
            )
        }
    }

    override fun onPINTextChanged(text: String) {
        setPINState {
            copy(
                sphinxPIN = text,
                errorMessage = null
            )
        }
    }

    fun onSubmitKeys() {
        RedemptionCode.decode(
            state.sphinxKeys
        )?.let { redemptionCode ->
            if (redemptionCode is RedemptionCode.AccountRestoration) {
                LandingScreenState.screenState(LandingScreenType.ExistingUserPIN)
            } else {
                setState {
                    copy(errorMessage = "Keys not Account restoration. Try New User section.")
                }
            }
        } ?: run {
            setState {
                copy(errorMessage = "Invalid keys")
            }
        }
    }

    override fun onSubmitPIN() {
        RedemptionCode.decode(
            state.sphinxKeys
        )?.let { redemptionCode ->
            if (redemptionCode is RedemptionCode.AccountRestoration) {
                // TODO: Decrypt keys...
                scope.launch(SphinxContainer.appModule.dispatchers.default) {

                    val pin = pinState.sphinxPIN.toCharArray()


                    try {
                        val decryptedCode = redemptionCode.decrypt(
                            pin,
                            SphinxContainer.appModule.dispatchers
                        )

                        setState {
                            copy(
                                errorMessage = "Keys decrypted..."
                            )
                        }

                        keyRestore.restoreKeys(
                            privateKey = decryptedCode.privateKey,
                            publicKey = decryptedCode.publicKey,
                            userPin = pin,
                            relayUrl = decryptedCode.relayUrl,
                            authorizationToken = decryptedCode.authorizationToken,
                        ).collect { flowResponse ->

                            when (flowResponse) {
                                is KeyRestoreResponse.Success -> {
                                    OnboardingState.status(OnboardingStatus.Successful)
                                    // TODO: Might want to go the dashboard
                                }
//                                else -> {
//                                    setState {
//                                        copy(
//                                            errorMessage = "Invalid PIN: ${flowResponse.toString()}"
//                                        )
//                                    }
//                                }
                                KeyRestoreResponse.Error.InvalidUserPin -> {
                                    setPINState {
                                        copy(
                                            errorMessage = "Invalid PIN",
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.KeysAlreadyPresent -> {
                                    setPINState {
                                        copy(
                                            errorMessage = "Key already loaded",
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.KeysThatWereSetDidNotMatch -> {
                                    setPINState {
                                        copy(
                                            errorMessage = "Invalid Keys",
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.PrivateKeyWasEmpty -> {
                                    setPINState {
                                        copy(
                                            errorMessage = "Invalid Keys",
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.PublicKeyWasEmpty -> {
                                    setPINState {
                                        copy(
                                            errorMessage = "Invalid Keys",
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.NotifyState.EncryptingJavaWebToken -> {
                                    setPINState {
                                        copy(
                                            infoMessage = "Encrypting web token",
                                            errorMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.NotifyState.EncryptingKeysWithUserPin -> {
                                    setPINState {
                                        copy(
                                            infoMessage = "Saving keys...",
                                            errorMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.NotifyState.EncryptingRelayUrl -> {
                                    setPINState {
                                        copy(
                                            infoMessage = "Encryping Relay",
                                            errorMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.FailedToSecureKeys -> {
                                    setPINState {
                                        copy(
                                            infoMessage = "Failed to secure your keys",
                                            errorMessage = null
                                        )
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        setPINState {
                            copy(errorMessage = "Invalid PIN")
                        }
                    }
//                    LandingScreenState.screenState(LandingScreenType.ExistingUser)
                }

            } else {
                setPINState {
                    copy(errorMessage = "Keys not Account restoration. Try New User section.")
                }
            }
        } ?: run {
            setPINState {
                copy(errorMessage = "Invalid keys")
            }
        }
    }

    private fun initialState(): ExistingUserState = ExistingUserState()

    private inline fun setState(update: ExistingUserState.() -> ExistingUserState) {
        state = state.update()
    }
}