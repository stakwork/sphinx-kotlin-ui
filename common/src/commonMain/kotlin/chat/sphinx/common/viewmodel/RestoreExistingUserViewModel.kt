package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.authentication.KeyRestoreResponse
import chat.sphinx.authentication.model.RedemptionCode
import chat.sphinx.common.state.*
import chat.sphinx.di.container.SphinxContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RestoreExistingUserViewModel: PINHandlingViewModel() {

    val keyRestore = SphinxContainer.authenticationModule.keyRestore(
        SphinxContainer.networkModule.relayDataHandlerImpl
    )

    var state: RestoreExistingUserState by mutableStateOf(initialState())
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
        if(text.length==6){
            onSubmitPIN()
        }
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
                LandingScreenState.screenState(LandingScreenType.RestoreFromKeystore)
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

                        DashboardScreenState.screenState(DashboardScreenType.Unlocked)
//                        setState {
//                            copy(
//                                errorMessage = "Keys decrypted..."
//                            )
//                        }
                        LandingScreenState.screenState(LandingScreenType.Loading)
                        setState {
                            copy(
                                errorMessage = null,
//                                isLoading = true
                            )
                        }
                        delay(1000L)
                        keyRestore.restoreKeys(
                            privateKey = decryptedCode.privateKey,
                            publicKey = decryptedCode.publicKey,
                            userPin = pin,
                            relayUrl = decryptedCode.relayUrl,
                            authorizationToken = decryptedCode.authorizationToken,
                        ).collect { flowResponse ->

                            when (flowResponse) {
                                is KeyRestoreResponse.Success -> {
                                    setState {
                                        copy(
                                            errorMessage = null,
                                            isLoading = false,
                                            infoMessage = "success"
                                        )
                                    }
                                    LandingScreenState.screenState(LandingScreenType.RestoreExistingUserSuccess)
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
                                    setState {
                                        copy(
                                            errorMessage = "Invalid PIN",
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.KeysAlreadyPresent -> {
                                    setState {
                                        copy(
                                            errorMessage = "Key already loaded",
                                            isLoading = false,
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.KeysThatWereSetDidNotMatch -> {
                                    setState {
                                        copy(
                                            errorMessage = "Invalid Keys",
                                            isLoading = false,
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.PrivateKeyWasEmpty -> {
                                    setState {
                                        copy(
                                            errorMessage = "Invalid Keys",
                                            isLoading = false,
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.PublicKeyWasEmpty -> {
                                    setState {
                                        copy(
                                            errorMessage = "Invalid Keys",
                                            isLoading = false,
                                            infoMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.NotifyState.EncryptingJavaWebToken -> {
                                    setState {
                                        copy(
                                            infoMessage = "Encrypting web token",
                                            isLoading = false,
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
                                    setState {
                                        copy(
                                            infoMessage = "Encryping Relay",
                                            isLoading = false,
                                            errorMessage = null
                                        )
                                    }
                                }
                                KeyRestoreResponse.Error.FailedToSecureKeys -> {
                                    setState {
                                        copy(
                                            infoMessage = "Failed to secure your keys",
                                            isLoading = false,
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

    private fun initialState(): RestoreExistingUserState = RestoreExistingUserState()

    private inline fun setState(update: RestoreExistingUserState.() -> RestoreExistingUserState) {
        state = state.update()
    }
}