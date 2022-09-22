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
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.network.query.chat.model.TribeDto
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
import chat.sphinx.response.message
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.chat.ChatHost
import chat.sphinx.wrapper.chat.ChatUUID
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.invite.InviteString
import chat.sphinx.wrapper.invite.toValidInviteStringOrNull
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.NodeBalanceAll
import chat.sphinx.wrapper.lightning.toLightningNodePubKey
import chat.sphinx.wrapper.lightning.toLightningRouteHint
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.toFileName
import chat.sphinx.wrapper.relay.*
import chat.sphinx.wrapper.rsa.RsaPublicKey
import chat.sphinx.wrapper.tribe.toTribeJoinLink
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okio.Path
import theme.badge_red

class SignUpViewModel : PinAuthenticationViewModel() {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val authenticationManager = SphinxContainer.authenticationModule.authenticationCoreManager
    private val networkModule = SphinxContainer.networkModule
    private val repositoryModule = SphinxContainer.repositoryModule(sphinxNotificationManager)
    private val networkQueryInvite: NetworkQueryInvite = networkModule.networkQueryInvite
    private val networkQueryRelayKeys: NetworkQueryRelayKeys = networkModule.networkQueryRelayKeys
    private val networkQueryChat: NetworkQueryChat = networkModule.networkQueryChat
    private val relayDataHandler = networkModule.relayDataHandler
    private val networkQueryContact = networkModule.networkQueryContact
    private val rsa = SphinxContainer.authenticationModule.rsa
    private val chatRepository = repositoryModule.chatRepository
    private val contactRepository = repositoryModule.contactRepository
    private val lightningRepository = repositoryModule.lightningRepository
    private val onBoardStepHandler = OnBoardStepHandler()

    companion object {
        private const val PLANET_SPHINX_TRIBE =
            "sphinx.chat://?action=tribe&uuid=X3IWAiAW5vNrtOX5TLEJzqNWWr3rrUaXUwaqsfUXRMGNF7IWOHroTGbD4Gn2_rFuRZcsER0tZkrLw3sMnzj4RFAk_sx0&host=tribes.sphinx.chat"
    }

    init {
        scope.launch(dispatchers.mainImmediate) {
            restoreSignupStep()
        }
    }

    override fun onAuthenticationSucceed() {
        scope.launch(dispatchers.mainImmediate) {
            onBoardStepHandler.retrieveOnBoardStep()?.let { onBoardStep ->
                when (onBoardStep) {
                    is OnBoardStep.Step2_Name -> {
                        setPinFields(pinState.sphinxPIN)
                        LandingScreenState.screenState(LandingScreenType.OnBoardLightningBasicInfo)
                    }
                    is OnBoardStep.Step3_Picture -> {
                        LandingScreenState.screenState(LandingScreenType.OnBoardLightningProfilePicture)
                    }
                    is OnBoardStep.Step4_Ready -> {
                        reloadAccountData()
                        LandingScreenState.screenState(LandingScreenType.OnBoardLightningReady)
                    }
                }
            }
        }
    }

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

    inline fun setSignupBasicInfoState(update: SignupBasicInfoState.() -> SignupBasicInfoState) {
        signupBasicInfoState = signupBasicInfoState.update()
    }

    fun navigateTo(screenState: LandingScreenType) {
        LandingScreenState.screenState(screenState)
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

        setSignupBasicInfoState {
            copy(
                userPicture = AttachmentInfo(
                    filePath = filepath,
                    mediaType = mediaType,
                    fileName = filepath.name.toFileName(),
                    isLocalFile = true
                )
            )
        }
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
                } else {
                    toast("Pin doesn't match")
                }
            }
        }
        setSignupBasicInfoState {
            copy(
                basicInfoButtonEnabled = false
            )
        }
    }

    fun onSubmitInvitationCode() {
        scope.launch(dispatchers.mainImmediate) {
            val code = signupCodeState.invitationCodeText
            val inviteCode = code.toValidInviteStringOrNull()

            if (inviteCode != null) {
                LandingScreenState.screenState(LandingScreenType.Loading)
                redeemInvite(inviteCode)
                return@launch
            }

            val redemptionCode = RedemptionCode.decode(code)
            if (redemptionCode != null && redemptionCode is RedemptionCode.NodeInvite) {
                LandingScreenState.screenState(LandingScreenType.Loading)
                getTransportKey(
                    ip = redemptionCode.ip,
                    nodePubKey = null,
                    password = redemptionCode.password,
                    redeemInviteDto = null
                )
                return@launch
            }

            toast("The code your entered is not a valid invite or connection code")
        }
    }

    private suspend fun redeemInvite(input: InviteString) {
        networkQueryInvite.redeemInvite(input.value).collect { loadResponse ->
            when (loadResponse) {
                is LoadResponse.Loading -> {}
                is Response.Error -> {
                    toast("There was a problem with the invitation code, please try later ${loadResponse.message}")
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
                    toast("Generate token failed")
                    LandingScreenState.screenState(LandingScreenType.NewUser)
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
                    toast("Error persisting signup step. Please try again later.")
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
                            is Response.Error -> {
                                toast("Error creating HMACKey")
                            }
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
    fun onSubmitNicknameAndPin() {
        if (submitJob?.isActive == true) {
            return
        }

        setSignupBasicInfoState {
            copy(
                showLoading = true
            )
        }

        submitJob = scope.launch(dispatchers.mainImmediate) {
            val input = authenticationManager.getNewUserInput()
            val userInput = signupBasicInfoState.newPin.toCharArray()

            var userPinBuilt = true
            for (c in userInput) {
                try {
                    input.addCharacter(c)
                } catch (e: IllegalArgumentException) {
                    showError("Invalid PIN")
                    userPinBuilt = false
                    break
                }
            }

            if (userPinBuilt) {
                val request = AuthenticationRequest.LogIn(privateKey = null)
                var completionResponse: AuthenticationResponse.Success.Authenticated? = null
                var confirmToSetPin: AuthenticateFlowResponse.ConfirmInputToSetForFirstTime? = null

                authenticationManager.authenticate(input, listOf(request)).collect { flowResponse ->
                    if (flowResponse is AuthenticateFlowResponse.ConfirmInputToSetForFirstTime) {
                        confirmToSetPin = flowResponse
                    } else if (
                        flowResponse is AuthenticateFlowResponse.Success &&
                        flowResponse.requests.size == 1 &&
                        flowResponse.requests[0] is AuthenticationResponse.Success.Authenticated
                    ) {
                        completionResponse = flowResponse.requests[0] as AuthenticationResponse.Success.Authenticated
                    } else if (flowResponse is AuthenticateFlowResponse.WrongPin) {
                        showError("The PIN you entered is invalid. A PIN was already set")

                        setSignupBasicInfoState {
                            copy(
                                newPin = "",
                                confirmedPin = ""
                            )
                        }
                    }
                }

                confirmToSetPin?.let { setPin ->
                    authenticationManager.setPasswordFirstTime(setPin, input, listOf(request))
                        .collect { flowResponse ->
                            if (
                                flowResponse is AuthenticateFlowResponse.Success &&
                                flowResponse.requests.size == 1 &&
                                flowResponse.requests[0] is AuthenticationResponse.Success.Authenticated
                            ) {
                                completionResponse = flowResponse.requests[0] as AuthenticationResponse.Success.Authenticated
                            } else if (flowResponse is AuthenticateFlowResponse.Error) {
                                showError("There was an error while setting your PIN")
                            }
                        }
                }

                completionResponse?.let { _ ->
                    authenticationManager.getEncryptionKey()?.let { encryptionKey ->
                        (signupBasicInfoState.onboardStep as? OnBoardStep.Step1_WelcomeMessage)?.let { onboardStep1 ->
                            relayDataHandler.persistRelayUrl(onboardStep1.relayUrl)
                            relayDataHandler.persistAuthorizationToken(onboardStep1.authorizationToken)
                            relayDataHandler.persistRelayTransportKey(onboardStep1.transportKey)
                            relayDataHandler.persistRelayHMacKey(onboardStep1.hMacKey)
                        }

                        val step2Message: OnBoardStep.Step2_Name? =
                            onBoardStepHandler.persistOnBoardStep2Data(
                                signupBasicInfoState.onboardStep?.inviterData
                            )

                        if (step2Message == null) {
                            showError("Error persisting signup step. Please try again later")
                            return@let
                        } else {
                            setSignupBasicInfoState {
                                copy(
                                    onboardStep = step2Message
                                )
                            }
                        }

                        contactRepository.networkRefreshContacts.collect { refreshResponse ->
                            when (refreshResponse) {
                                is LoadResponse.Loading -> {}
                                is Response.Error -> {
                                    showError("Error retrieving contacts. Please try again")
                                }
                                is Response.Success -> {
                                    contactRepository.updateOwnerNameAndKey(
                                        signupBasicInfoState.nickname,
                                        encryptionKey.publicKey
                                    ).let { updateOwnerResponse ->
                                        when (updateOwnerResponse) {
                                            is Response.Error -> {
                                                showError("Error updating owner nickname. Please try again")
                                            }
                                            is Response.Success -> {
                                                val step3Message: OnBoardStep.Step3_Picture? =
                                                    onBoardStepHandler.persistOnBoardStep3Data(
                                                        signupBasicInfoState.onboardStep?.inviterData
                                                    )

                                                if (step3Message == null) {
                                                    showError("Error persisting signup step. Please try again later")
                                                } else {
                                                    setSignupBasicInfoState {
                                                        copy(
                                                            onboardStep = step3Message,
                                                            showLoading = false
                                                        )
                                                    }
                                                    navigateTo(LandingScreenType.OnBoardLightningProfilePicture)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } ?: {
                        showError("Error retrieving your encryption keys. Please try again")
                    }
                }
            }
        }
    }

    fun updateProfilePic() {
        if (submitJob?.isActive == true) {
            return
        }

        getBalances()

        submitJob = scope.launch(dispatchers.mainImmediate) {
            signupBasicInfoState.userPicture?.let {
                setSignupBasicInfoState {
                    copy(
                        showLoading = true
                    )
                }

                contactRepository.updateProfilePic(
                    path = it.filePath,
                    mediaType = it.mediaType,
                    fileName = it.fileName?.value ?: "unknown",
                    contentLength = null
                ).let { response ->
                    when (response) {
                        is Response.Error -> {
                            showError("Error updating profile picture, please try again")
                        }
                        is Response.Success -> {
                            continueToEndScreen()
                        }
                    }
                }
            } ?: run {
                continueToEndScreen()
            }
        }
    }

    private suspend fun continueToEndScreen() {
        val step4Message: OnBoardStep.Step4_Ready? =
            onBoardStepHandler.persistOnBoardStep4Data(
                signupBasicInfoState.onboardStep?.inviterData
            )

        if (step4Message == null) {
            showError("Error persisting signup step. Please try again later")
        } else {
            setSignupBasicInfoState {
                copy(
                    onboardStep = step4Message,
                    showLoading = false
                )
            }
            navigateTo(LandingScreenType.OnBoardLightningReady)
        }
    }

    private fun getBalances() {
        scope.launch(dispatchers.mainImmediate) {
            lightningRepository.getAccountBalanceAll().collect { loadResponse ->
                when (loadResponse) {
                    LoadResponse.Loading -> {}
                    is Response.Error -> {}
                    is Response.Success -> {
                        val balance = loadResponse.value
                        val localBalance = balance.localBalance
                        val remoteBalance = balance.remoteBalance

                        setSignupBasicInfoState {
                            copy(
                                balance = NodeBalanceAll(localBalance, remoteBalance)
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadOwner() {
        val owner = contactRepository.accountOwner.value.let { contact ->
            if (contact != null) {
                contact
            } else {
                var resolvedOwner: Contact? = null
                try {
                    contactRepository.accountOwner.collect { ownerContact ->
                        if (ownerContact != null) {
                            resolvedOwner = ownerContact
                            throw Exception()
                        }
                    }
                } catch (e: Exception) {
                }
                delay(25L)

                resolvedOwner!!
            }
        }
        setSignupBasicInfoState {
            copy(
                nickname = owner.alias?.value ?: "Unknown",
                userPhotoUrl = owner.photoUrl
            )
        }
    }

    fun onReadySubmit() {
        if (submitJob?.isActive == true) {
            return
        }

        setSignupBasicInfoState {
            copy(
                showLoading = true
            )
        }
        submitJob = scope.launch(dispatchers.mainImmediate) {
            signupBasicInfoState.onboardStep?.inviterData?.let {
                if (it.nickname?.isNotEmpty() == true && it.pubkey?.value?.isNotEmpty() == true) {
                    saveInviterAndFinish(it.nickname!!, it.pubkey!!.value, it.routeHint, it.pin)
                } else if (it.pin?.isNotEmpty() == true) {
                    finishInvite(it.pin!!)
                } else {
                    loadAndJoinDefaultTribeData()
                }
            }
        }
    }

    private fun saveInviterAndFinish(
        nickname: String,
        pubkey: String,
        routeHint: String?,
        inviteString: String? = null
    ) {
        scope.launch(dispatchers.mainImmediate) {
            val alias = ContactAlias(nickname)
            val pubKey = LightningNodePubKey(pubkey)
            val lightningRouteHint = routeHint?.toLightningRouteHint()

            contactRepository.createContact(
                alias,
                pubKey,
                lightningRouteHint
            ).collect { loadResponse ->
                when (loadResponse) {
                    LoadResponse.Loading -> {}
                    else -> {
                        if (inviteString != null && inviteString.isNotEmpty()) {
                            finishInvite(inviteString)
                        } else {
                            loadAndJoinDefaultTribeData()
                        }
                    }
                }
            }
        }
    }

    private fun finishInvite(inviteString: String) {
        scope.launch(dispatchers.mainImmediate) {
            networkQueryInvite.finishInvite(inviteString).collect { loadResponse ->
                when (loadResponse) {
                    is LoadResponse.Loading -> {}
                    else -> {
                        loadAndJoinDefaultTribeData()
                    }
                }
            }
        }
    }

    private fun loadAndJoinDefaultTribeData() {
        scope.launch(dispatchers.mainImmediate) {
            PLANET_SPHINX_TRIBE.toTribeJoinLink()?.let { tribeJoinLink ->

                networkQueryChat.getTribeInfo(
                    ChatHost(tribeJoinLink.tribeHost),
                    ChatUUID(tribeJoinLink.tribeUUID)
                ).collect { loadResponse ->
                    when (loadResponse) {
                        is LoadResponse.Loading -> {}
                        is Response.Error -> {
                            continueToSphinxOnYourPhone()

                        }
                        is Response.Success -> {
                            val tribeInfo = loadResponse.value
                            tribeInfo.set(tribeJoinLink.tribeHost, tribeJoinLink.tribeUUID)
                            joinDefaultTribe(tribeInfo)
                        }
                    }
                }
            } ?: continueToSphinxOnYourPhone()
        }
    }

    private fun joinDefaultTribe(tribeInfo: TribeDto) {
        scope.launch(dispatchers.mainImmediate) {
            tribeInfo.amount = tribeInfo.price_to_join

            chatRepository.joinTribe(tribeInfo).collect { loadResponse ->
                when (loadResponse) {
                    LoadResponse.Loading -> {}

                    is Response.Error -> {
                        continueToSphinxOnYourPhone()
                    }
                    is Response.Success -> {
                        continueToSphinxOnYourPhone()
                    }
                }
            }
        }
    }

    private fun continueToSphinxOnYourPhone() {
        setSignupBasicInfoState {
            copy(
                showLoading = false
            )
        }
        scope.launch(dispatchers.mainImmediate) {
            onBoardStepHandler.finishOnBoardSteps()
            LandingScreenState.screenState(LandingScreenType.OnBoardSphinxOnYourPhone)
        }
    }

    fun continueToDashboard() {
        DashboardScreenState.screenState(DashboardScreenType.Unlocked)
        AppState.screenState(ScreenType.DashboardScreen)
        LandingScreenState.screenState(LandingScreenType.LandingPage)
    }

    private fun showError(error: String) {
        setSignupBasicInfoState {
            copy(
                showLoading = false
            )
        }
        toast(error)
    }

    fun toast(
        message: String,
        color: Color = badge_red,
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

    private suspend fun restoreSignupStep() {
        onBoardStepHandler.retrieveOnBoardStep()?.let { onBoardStep ->
            setSignupBasicInfoState {
                copy(
                    onboardStep = onBoardStep
                )
            }

            if (onBoardStep is OnBoardStep.Step1_WelcomeMessage) {
                setSignupInviterState {
                    copy(
                        welcomeMessage = onBoardStep.inviterData.message ?: "Welcome to Sphinx!",
                        friendName = onBoardStep.inviterData.nickname ?: "Sphinx Support"
                    )
                }
            }
        }
    }

    fun setPinFields(pin: String) {
        setSignupBasicInfoState {
            copy(
                newPin = pin,
                confirmedPin = pin,
            )
        }
    }

    fun reloadAccountData() {
        scope.launch(dispatchers.mainImmediate) {
            loadOwner()
            getBalances()
        }
    }

}
