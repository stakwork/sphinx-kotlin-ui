package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.authentication.KeyRestoreResponse
import chat.sphinx.authentication.model.RedemptionCode
import chat.sphinx.common.state.*
import chat.sphinx.concepts.network.query.relay_keys.model.PostHMacKeyDto
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.annotations.UnencryptedDataAccess
import chat.sphinx.crypto.common.clazzes.*
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.wrapper.relay.*
import chat.sphinx.wrapper.rsa.RsaPrivateKey
import chat.sphinx.wrapper.rsa.RsaPublicKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RestoreExistingUserViewModel: PINHandlingViewModel() {

    private val keyRestore = SphinxContainer.authenticationModule.keyRestore(
        SphinxContainer.networkModule.relayDataHandlerImpl
    )
    private val relayDataHandler = SphinxContainer.networkModule.relayDataHandlerImpl
    private val networkQueryRelayKeys = SphinxContainer.networkModule.networkQueryRelayKeys
    private val rsa = SphinxContainer.authenticationModule.rsa

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
                    copy(errorMessage = "Invalid Restore string")
                }
                LandingScreenState.screenState(LandingScreenType.RestoreFromKeystore)
            }
        } ?: run {
            setState {
                copy(errorMessage = "Invalid Restore string")
            }
        }
    }

    @OptIn(RawPasswordAccess::class)
    override fun onSubmitPIN() {
        RedemptionCode.decode(
            state.sphinxKeys
        )?.let { redemptionCode ->
            if (redemptionCode is RedemptionCode.AccountRestoration) {

                LandingScreenState.screenState(LandingScreenType.Loading)

                scope.launch(dispatchers.default) {

                    val pin = pinState.sphinxPIN.toCharArray()

                    try {
                        val decryptedCode = redemptionCode.decrypt(
                            pin,
                            SphinxContainer.appModule.dispatchers
                        )

                        setState {
                            copy(
                                errorMessage = null,
                                isLoading = true
                            )
                        }

                        val relayUrl = relayDataHandler.formatRelayUrl(decryptedCode.relayUrl)

                        var transportKey: RsaPublicKey? = null

                        networkQueryRelayKeys.getRelayTransportKey(relayUrl).collect { loadResponse ->
                            Exhaustive@
                            when (loadResponse) {
                                is LoadResponse.Loading -> {}
                                is Response.Error -> {}

                                is Response.Success -> {
                                    transportKey = RsaPublicKey(loadResponse.value.transport_key.toCharArray())
                                }
                            }
                        }

                        var ownerPrivateKey = RsaPrivateKey(
                            Password(decryptedCode.privateKey.value.copyOf()).value
                        )

                        var success: KeyRestoreResponse.Success? = null

                        delay(1000L)

                        keyRestore.restoreKeys(
                            privateKey = decryptedCode.privateKey,
                            publicKey = decryptedCode.publicKey,
                            userPin = pin,
                            relayUrl = decryptedCode.relayUrl,
                            authorizationToken = decryptedCode.authorizationToken,
                        ).collect { response ->
                            when (response) {
                                is KeyRestoreResponse.Success -> {
                                    success = response
                                }
                                is KeyRestoreResponse.Error -> {
                                    handleRestoreKeysError(response)
                                    LandingScreenState.screenState(LandingScreenType.ExistingUserPIN)
                                }
                            }
                        }

                        success?.let { _ ->
                            setState {
                                copy(
                                    errorMessage = null,
                                    isLoading = false,
                                    infoMessage = "success"
                                )
                            }
                            goToConnectedScreen(
                                ownerPrivateKey,
                                transportKey
                            )
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        setPINState {
                            copy(
                                errorMessage = "Invalid PIN"
                            )
                        }
                        LandingScreenState.screenState(LandingScreenType.ExistingUserPIN)
                    }
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

    private fun handleRestoreKeysError(response: KeyRestoreResponse) {
        when (response) {
            is KeyRestoreResponse.Error.InvalidUserPin -> {
                setState {
                    copy(
                        errorMessage = "Invalid PIN",
                        infoMessage = null
                    )
                }
            }
            is KeyRestoreResponse.Error.KeysAlreadyPresent -> {
                setState {
                    copy(
                        errorMessage = "Key already loaded",
                        isLoading = false,
                        infoMessage = null
                    )
                }
            }
            is KeyRestoreResponse.Error.KeysThatWereSetDidNotMatch -> {
                setState {
                    copy(
                        errorMessage = "Invalid Keys",
                        isLoading = false,
                        infoMessage = null
                    )
                }
            }
            is KeyRestoreResponse.Error.PrivateKeyWasEmpty -> {
                setState {
                    copy(
                        errorMessage = "Invalid Keys",
                        isLoading = false,
                        infoMessage = null
                    )
                }
            }
            is KeyRestoreResponse.Error.PublicKeyWasEmpty -> {
                setState {
                    copy(
                        errorMessage = "Invalid Keys",
                        isLoading = false,
                        infoMessage = null
                    )
                }
            }
            is KeyRestoreResponse.NotifyState.EncryptingJavaWebToken -> {
                setState {
                    copy(
                        infoMessage = "Encrypting web token",
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            is KeyRestoreResponse.NotifyState.EncryptingKeysWithUserPin -> {
                setPINState {
                    copy(
                        infoMessage = "Saving keys...",

                        errorMessage = null
                    )
                }
            }
            is KeyRestoreResponse.NotifyState.EncryptingRelayUrl -> {
                setState {
                    copy(
                        infoMessage = "Encryping Relay",
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            is KeyRestoreResponse.Error.FailedToSecureKeys -> {
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

    private suspend fun goToConnectedScreen(
        ownerPrivateKey: RsaPrivateKey,
        transportKey: RsaPublicKey?
    ) {
        scope.launch(dispatchers.mainImmediate) {
            getOrCreateHMacKey(
                ownerPrivateKey,
                transportKey
            )
        }.join()

        LandingScreenState.screenState(LandingScreenType.RestoreExistingUserSuccess)
        OnboardingState.status(OnboardingStatus.Successful)
    }

    @OptIn(UnencryptedDataAccess::class)
    private suspend fun getOrCreateHMacKey(
        ownerPrivateKey: RsaPrivateKey,
        transportKey: RsaPublicKey?
    ) {
        if (transportKey == null) {
            return
        }

        networkQueryRelayKeys.getRelayHMacKey().collect { loadResponse ->
            Exhaustive@
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    createHMacKey(
                        transportKey = transportKey
                    )?.let { relayHMacKey ->
                        relayDataHandler.persistRelayHMacKey(relayHMacKey)
                    }
                }
                is Response.Success -> {
                    val response = rsa.decrypt(
                        rsaPrivateKey = ownerPrivateKey,
                        text = EncryptedString(loadResponse.value.encrypted_key),
                        dispatcher = dispatchers.default
                    )

                    when (response) {
                        is Response.Error -> {}
                        is Response.Error -> {}
                        is Response.Success -> {
                            relayDataHandler.persistRelayHMacKey(
                                RelayHMacKey(
                                    response.value.toUnencryptedString(trim = false).value
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun createHMacKey(
        relayData: Triple<Pair<AuthorizationToken, TransportToken?>, RequestSignature?, RelayUrl>? = null,
        transportKey: RsaPublicKey?
    ): RelayHMacKey? {
        var hMacKey: RelayHMacKey? = null

        if (transportKey == null) {
            return null
        }

        scope.launch(dispatchers.mainImmediate) {

            @OptIn(RawPasswordAccess::class)
            val hMacKeyString =
                PasswordGenerator(passwordLength = 20).password.value.joinToString("")

            val encryptionResponse = rsa.encrypt(
                transportKey,
                UnencryptedString(hMacKeyString),
                formatOutput = false,
                dispatcher = dispatchers.default,
            )

            when (encryptionResponse) {
                is Response.Error -> {
                }
                is Response.Success -> {
                    networkQueryRelayKeys.addRelayHMacKey(
                        PostHMacKeyDto(encryptionResponse.value.value),
                        relayData
                    ).collect { loadResponse ->
                        Exhaustive@
                        when (loadResponse) {
                            is LoadResponse.Loading -> {
                            }
                            is Response.Error -> {}
                            is Response.Success -> {
                                hMacKey = RelayHMacKey(hMacKeyString)
                            }
                        }
                    }
                }
            }

        }.join()

        return hMacKey
    }

    private fun initialState(): RestoreExistingUserState = RestoreExistingUserState()

    private inline fun setState(update: RestoreExistingUserState.() -> RestoreExistingUserState) {
        state = state.update()
    }
}