package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import chat.sphinx.authentication.model.OnBoardInviterData
import chat.sphinx.authentication.model.OnBoardStep
import chat.sphinx.authentication.model.OnBoardStepHandler
import chat.sphinx.authentication.model.RedemptionCode
import chat.sphinx.common.state.*
import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.concepts.authentication.coordinator.AuthenticationResponse
import chat.sphinx.concepts.network.query.contact.model.GenerateTokenResponse
import chat.sphinx.concepts.network.query.invite.NetworkQueryInvite
import chat.sphinx.concepts.network.query.invite.model.RedeemInviteDto
import chat.sphinx.concepts.network.query.relay_keys.NetworkQueryRelayKeys
import chat.sphinx.concepts.network.query.relay_keys.model.PostHMacKeyDto
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.clazzes.PasswordGenerator
import chat.sphinx.crypto.common.clazzes.UnencryptedString
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.invite.InviteString
import chat.sphinx.wrapper.invite.toValidInviteStringOrNull
import chat.sphinx.wrapper.lightning.toLightningNodePubKey
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.toFileName
import chat.sphinx.wrapper.relay.*
import chat.sphinx.wrapper.rsa.RsaPublicKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okio.Path
import kotlinx.coroutines.flow.collect
import theme.primary_red

class SignUpViewModel {

    private val authenticationManager = SphinxContainer.authenticationModule.authenticationCoreManager
    private val networkModule = SphinxContainer.networkModule
    private val networkQueryInvite: NetworkQueryInvite = networkModule.networkQueryInvite
    private val networkQueryRelayKeys: NetworkQueryRelayKeys = networkModule.networkQueryRelayKeys
    private val relayDataHandler = networkModule.relayDataHandler
    private val networkQueryContact = networkModule.networkQueryContact
    private val onBoardStepHandler = OnBoardStepHandler()
    private val rsa = SphinxContainer.authenticationModule.rsa
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers


    //SIGNUP CODE STATE
    var signupCodeState: SignupCodeState by mutableStateOf(initialSignupCodeState())

    private fun initialSignupCodeState(): SignupCodeState = SignupCodeState()

    private inline fun setSignupCodeState(update: SignupCodeState.() -> SignupCodeState) {
        signupCodeState = signupCodeState.update()
    }

    fun onInvitationCodeTextChanged(text: String) {
        setSignupCodeState {
            copy(
                invitationCodeText = text,
                errorMessage = null
            )
        }
    }

    var signupInviterState: SignupInviterState by mutableStateOf(initialSignupInviterState())

    private fun initialSignupInviterState(): SignupInviterState = SignupInviterState()

    private inline fun setSignupInviterState(update: SignupInviterState.() -> SignupInviterState) {
        signupInviterState = signupInviterState.update()
    }

    var signupBasicInfoState: SignupBasicInfoState by mutableStateOf(initialSignupBasicInfoState())

    private fun initialSignupBasicInfoState(): SignupBasicInfoState = SignupBasicInfoState()

    private inline fun setSignupBasicInfoState(update: SignupBasicInfoState.() -> SignupBasicInfoState) {
        signupBasicInfoState = signupBasicInfoState.update()
    }

    fun navigateTo(screenState: LightningScreenState) {
        setSignupBasicInfoState {
            copy(
                lightningScreenState = screenState
            )
        }
    }

    fun onNicknameChanged(nickname: String) {
        setSignupBasicInfoState {
            copy(
                nickname = nickname
            )
        }
        checkValidInput()
    }

    fun onNewPinChanged(newPin: String) {
        setSignupBasicInfoState {
            copy(
                newPin = newPin
            )
        }
        checkValidInput()
    }

    fun onConfirmedPinChanged(confirmedPin: String) {
        setSignupBasicInfoState {
            copy(
                confirmedPin = confirmedPin
            )
        }
        checkValidInput()
    }

    fun onProfilePictureChanged(filepath: Path) {
        val ext = filepath.toFile().extension
        val mediaType = MediaType.Image(MediaType.IMAGE + "/$ext")

        signupBasicInfoState.userPicture.value = AttachmentInfo(
            filePath = filepath,
            mediaType = mediaType,
            fileName = filepath.name.toFileName(),
            isLocalFile = true
        )

    }

    private fun checkValidInput() {
        signupBasicInfoState.apply {
            if (nickname.isNotEmpty() && newPin.length == 6 && confirmedPin.length == 6) {
                if (newPin == confirmedPin) {
                    setSignupBasicInfoState {
                        copy(
                            basicInfoButtonEnabled = true
                        )
                    }
                    return
                }
            }
        }
        setSignupBasicInfoState {
            copy(
                basicInfoButtonEnabled = false
            )
        }
    }

    fun toast(
        message: String,
        color: Color = primary_red,
        delay: Long = 2000L
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.toast(
                "Sphinx",
                message,
                color.value,
                delay
            )
        }
    }

    fun onSubmitInvitationCode() {
        val code = signupCodeState.invitationCodeText
        val inviteCode = code.toValidInviteStringOrNull()

        if (inviteCode != null) {
            LandingScreenState.screenState(LandingScreenType.Loading)

            scope.launch(dispatchers.mainImmediate) {
                redeemInvite(inviteCode)
            }
        }

        val redemptionCode = RedemptionCode.decode(code)
        if (redemptionCode != null && redemptionCode is RedemptionCode.NodeInvite) {
            LandingScreenState.screenState(LandingScreenType.Loading)

            scope.launch(dispatchers.mainImmediate) {
                getTransportKey(
                    ip = redemptionCode.ip,
                    nodePubKey = null,
                    password = redemptionCode.password,
                    redeemInviteDto = null
                )
            }
        }
    }

    private suspend fun redeemInvite(input: InviteString) {
        networkQueryInvite.redeemInvite(input).collect { loadResponse ->
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    toast("There was a problem with the invitation code, please try later")
                    LandingScreenState.screenState(LandingScreenType.NewUser)

                }
                is Response.Success -> {
                    val inviteResponse = loadResponse.value.response

                    inviteResponse?.invite?.let { invite ->
                        getTransportKey(
                            ip = RelayUrl(inviteResponse.ip),
                            nodePubKey = inviteResponse.pubkey,
                            password = null,
                            redeemInviteDto = invite,
                        )
                    }
                }
            }
        }
    }

    private suspend fun getTransportKey(
        ip: RelayUrl,
        nodePubKey: String?,
        password: String?,
        redeemInviteDto: RedeemInviteDto?,
        token: AuthorizationToken? = null
    ) {
        val relayUrl = relayDataHandler.formatRelayUrl(ip)
        networkModule.networkClient.setTorRequired(relayUrl.isOnionAddress)
        var transportKey: RsaPublicKey? = null

        networkQueryRelayKeys.getRelayTransportKey(relayUrl).collect { loadResponse ->
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    toast("There was a problem with your code, please try again")
                    LandingScreenState.screenState(LandingScreenType.NewUser)
                }
                is Response.Success -> {
                    transportKey = RsaPublicKey(loadResponse.value.transport_key.toCharArray())
                }
            }
        }

        registerTokenAndStartOnBoard(
            ip,
            nodePubKey,
            password,
            redeemInviteDto,
            token,
            transportKey
        )
    }

    private var tokenRetries = 0
    private suspend fun registerTokenAndStartOnBoard(
        ip: RelayUrl,
        nodePubKey: String?,
        password: String?,
        redeemInviteDto: RedeemInviteDto?,
        token: AuthorizationToken? = null,
        transportKey: RsaPublicKey? = null,
        transportToken: TransportToken? = null
    ) {

        @OptIn(RawPasswordAccess::class)
        val authToken = token ?: AuthorizationToken(
            PasswordGenerator(passwordLength = 20).password.value.joinToString("")
        )

        val relayUrl = relayDataHandler.formatRelayUrl(ip)
        networkModule.networkClient.setTorRequired(relayUrl.isOnionAddress)

        val inviterData: OnBoardInviterData? = redeemInviteDto?.let { dto ->
            OnBoardInviterData(
                dto.nickname,
                dto.pubkey?.toLightningNodePubKey(),
                dto.route_hint,
                dto.message,
                dto.action,
                dto.pin
            )
        }

        val relayTransportToken = transportToken ?: transportKey?.let { transportKey ->
            relayDataHandler.retrieveRelayTransportToken(
                authToken,
                transportKey
            )
        } ?: null

        var generateTokenResponse: LoadResponse<GenerateTokenResponse, ResponseError> = Response.Error(
            ResponseError("generateToken endpoint failed")
        )

        if (relayTransportToken != null) {
            networkQueryContact.generateToken(
                password,
                nodePubKey,
                Triple(Pair(authToken, relayTransportToken), null, relayUrl)
            ).collect { loadResponse ->
                generateTokenResponse = loadResponse
            }
        } else {
            networkQueryContact.generateToken(
                relayUrl,
                authToken,
                password,
                nodePubKey
            ).collect { loadResponse ->
                generateTokenResponse = loadResponse
            }
        }

        when (generateTokenResponse) {
            is LoadResponse.Loading -> {}
            is Response.Error -> {
                if (tokenRetries < 3) {
                    tokenRetries += 1

                    registerTokenAndStartOnBoard(
                        ip,
                        nodePubKey,
                        password,
                        redeemInviteDto,
                        authToken,
                        transportKey,
                        relayTransportToken
                    )
                } else {
                    toast("Generate token has failed")
                    LandingScreenState.screenState(LandingScreenType.NewUser)

//                    navigator.popBackStack()
                }
            }
            is Response.Success -> {

                val hMacKey = createHMacKey(
                    relayData = Triple(Pair(authToken, relayTransportToken), null, relayUrl),
                    transportKey = transportKey
                )

                val step1Message: OnBoardStep.Step1_WelcomeMessage? = onBoardStepHandler.persistOnBoardStep1Data(
                    relayUrl,
                    authToken,
                    transportKey,
                    hMacKey,
                    inviterData
                )

                if (step1Message == null) {
                    toast("Generate token has failed")
                    LandingScreenState.screenState(LandingScreenType.NewUser)

                } else {
                    setSignupBasicInfoState {
                        copy(
                            onboardStep = step1Message
                        )
                    }

                    setSignupInviterState {
                        copy(
                            welcomeMessage = step1Message.inviterData.message ?: "Welcome to Sphinx!",
                            friendName = step1Message.inviterData.nickname ?: "Sphinx Support"
                        )
                    }
                    LandingScreenState.screenState(LandingScreenType.OnBoardMessage)

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

    private var submitJob: Job? = null

    @OptIn(RawPasswordAccess::class)
    fun onSubmitNicknameAndPin() {
        setSignupBasicInfoState {
            copy(
                submitProgressBar = true
            )
        }
        if (submitJob?.isActive == true) {
            return
        }

        submitJob = scope.launch(dispatchers.mainImmediate) {
            val request = AuthenticationRequest.LogIn(privateKey = null)
            val input = authenticationManager.getNewUserInput()
            val userInput = signupBasicInfoState.newPin.toCharArray()

            var userPinBuilt = true
            for (c in userInput) {
                try {
                    input.addCharacter(c)
                } catch (e: IllegalArgumentException) {

                    setSignupBasicInfoState {
                        copy(
                            submitProgressBar = false
                        )
                    }
                    toast("Error Invalid PIN")
                    userPinBuilt = false
                    break
                }
            }

            if (userPinBuilt) {
                var confirmToSetPin: AuthenticateFlowResponse.ConfirmInputToSetForFirstTime? = null
                authenticationManager.authenticate(input, listOf(request)).collect { flowResponse ->
                    if (flowResponse is AuthenticateFlowResponse.ConfirmInputToSetForFirstTime) {
                        confirmToSetPin = flowResponse
                    }
                }

                confirmToSetPin?.let { setPin ->

                    var completionResponse: AuthenticationResponse.Success.Authenticated? = null
                    authenticationManager.setPasswordFirstTime(setPin, input, listOf(request))
                        .collect { flowResponse ->
                            if (
                                flowResponse is AuthenticateFlowResponse.Success &&
                                flowResponse.requests.size == 1 &&
                                flowResponse.requests[0] is AuthenticationResponse.Success.Authenticated
                            ) {
                                completionResponse =
                                    flowResponse.requests[0] as AuthenticationResponse.Success.Authenticated
                            }

                            if (flowResponse is AuthenticateFlowResponse.Error) {
                                setSignupBasicInfoState {
                                    copy(
                                        submitProgressBar = false
                                    )
                                }
                                toast("There was an error to set your PIN")
                            }
                        }

                    completionResponse?.let { _ ->
                        authenticationManager.getEncryptionKey()?.let { encryptionKey ->
                            (signupBasicInfoState.onboardStep as? OnBoardStep.Step1_WelcomeMessage)?.let { onboardStep1 ->
                                relayDataHandler.persistRelayUrl(onboardStep1.relayUrl)
                                relayDataHandler.persistAuthorizationToken(onboardStep1.authorizationToken)
                                relayDataHandler.persistRelayTransportKey(onboardStep1.transportKey)
                                relayDataHandler.persistRelayHMacKey(onboardStep1.hMacKey)

                                contactRepository.networkRefreshContacts.collect { refreshResponse ->

                                    when (refreshResponse) {
                                        is LoadResponse.Loading -> {}
                                        is Response.Error -> {

                                        }
                                        is Response.Success -> {
                                            contactRepository.updateOwnerNameAndKey(
                                                signupBasicInfoState.nickname,
                                                encryptionKey.publicKey
                                            ).let { updateOwnerResponse ->

                                                when (updateOwnerResponse) {
                                                    is Response.Error -> {
//                                                        toast("Error to update Owner")
                                                    }
                                                    is Response.Success -> {
//                                                        val step3: OnBoardStep.Step3_Picture? = onBoardStepHandler.persistOnBoardStep3Data(onboardStep1.inviterData)

//                                                        if (step3 != null) {
//
//                                                        }
//                                                        else {
//                                                            // TODO: Handle Error
//                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                val step2Message: OnBoardStep.Step2_NameAndPin? =
                                    onBoardStepHandler.persistOnBoardStep2Data(
                                        onboardStep1.inviterData
                                    )

                                if (step2Message == null) {
                                    toast("There was an error")
                                } else {
                                    setSignupBasicInfoState {
                                        copy(
                                            onboardStep = step2Message
                                        )
                                    }

                                    setSignupBasicInfoState {
                                        copy(
                                            lightningScreenState = LightningScreenState.ProfileImage
                                        )
                                    }
                                }
                            }
                        } ?: let {
                            toast("There was an error")
                        }
                    } ?: let {
                        toast("There was an error")

                    }

                } ?: {
                    toast("There was an error")
                }
            }
        }
    }

    fun updateProfilePic() {
        scope.launch(dispatchers.mainImmediate) {
            signupBasicInfoState.userPicture.value?.apply {
                contactRepository.updateProfilePic(
                    path = filePath,
                    mediaType = mediaType,
                    fileName = fileName?.value ?: "unknown",
                    contentLength = null
                )
            }
            val step3Message: OnBoardStep.Step3_Picture? =
                (signupBasicInfoState.onboardStep as? OnBoardStep.Step2_NameAndPin)?.let { step2 ->
                    onBoardStepHandler.persistOnBoardStep3Data(step2.inviterData)
                }
            if (step3Message == null) {
                toast("There was an error")
            } else {
                setSignupBasicInfoState {
                    copy(
                        onboardStep = step3Message
                    )
                }
            }
        }
    }

}