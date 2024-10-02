package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import chat.sphinx.authentication.model.OnBoardStep
import chat.sphinx.authentication.model.OnBoardStepHandler
import chat.sphinx.authentication.model.RedemptionCode
import chat.sphinx.common.state.*
import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.concepts.authentication.coordinator.AuthenticationResponse
import chat.sphinx.concepts.network.query.chat.model.TribeDto
import chat.sphinx.concepts.repository.connect_manager.model.OwnerRegistrationState
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.database.core.SphinxDatabaseQueries
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import chat.sphinx.features.repository.util.deleteAll
import chat.sphinx.response.*
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.toFileName
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.Path
import theme.badge_red

class SignUpViewModel : PinAuthenticationViewModel() {

    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val authenticationManager = SphinxContainer.authenticationModule.authenticationCoreManager
    private val networkModule = SphinxContainer.networkModule
    private val repositoryModule = SphinxContainer.repositoryModule(sphinxNotificationManager)
    private val relayDataHandler = networkModule.relayDataHandler
    private val chatRepository = repositoryModule.chatRepository
    private val contactRepository = repositoryModule.contactRepository
    private val connectManagerRepository = repositoryModule.connectManagerRepository
    private val onBoardStepHandler = OnBoardStepHandler()


    init {
        scope.launch(dispatchers.mainImmediate) {
            listenToOwnerRegistered()
            clearDatabase()
        }
    }
    fun clearDatabase() {
        SphinxContainer.appModule.coreDBImpl.getSphinxDatabaseQueriesOrNull()?.let { queries: SphinxDatabaseQueries ->
            queries.transaction {
                deleteAll(queries)
            }
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
                    else -> {}
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

    var state: RestoreExistingUserState by mutableStateOf(initialState())
        private set

    private fun initialState(): RestoreExistingUserState = RestoreExistingUserState()

    private inline fun setState(update: RestoreExistingUserState.() -> RestoreExistingUserState) {
        state = state.update()
    }

    var showSelectNetworkDialog = mutableStateOf(false)
        private set

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

    fun onKeysTextChanged(text: String) {
        setState {
            copy(
                sphinxKeys = text,
                errorMessage = null
            )
        }
    }

    fun onSubmitInvitationCode() {
        scope.launch(dispatchers.mainImmediate) {
            val code = signupCodeState.invitationCodeText

            val redemptionCode = RedemptionCode.decode(code)

            if (redemptionCode != null && redemptionCode is RedemptionCode.NodeInvite) {
                LandingScreenState.screenState(LandingScreenType.Loading)
                return@launch
            }

            if (redemptionCode != null && redemptionCode is RedemptionCode.SwarmConnect) {
                LandingScreenState.screenState(LandingScreenType.Loading)
                return@launch
            }
            if (redemptionCode != null && redemptionCode is RedemptionCode.SwarmClaim) {
                LandingScreenState.screenState(LandingScreenType.Loading)
            }
            if (redemptionCode != null && redemptionCode is RedemptionCode.NewInvite) {
                connectManagerRepository.setInviteCode(redemptionCode.code)
                LandingScreenState.screenState(LandingScreenType.OnBoardLightningBasicInfo)
            }
        }
    }

    fun onSubmitKeys() {
        RedemptionCode.decode(
            state.sphinxKeys
        )?.let { redemptionCode ->

            if (redemptionCode is RedemptionCode.MnemonicRestoration) {
                connectManagerRepository.setMnemonicWords(redemptionCode.mnemonic)
                showSelectNetworkDialog.value = true
            } else {
                setState {
                    copy(errorMessage = "Invalid Restore string")
                }
            }
        } ?: run {
            setState {
                copy(errorMessage = "Invalid Restore string")
            }
        }
    }

    fun onNetworkTypeSelected(isTestEnvironment: Boolean) {
        connectManagerRepository.setNetworkType(isTestEnvironment)
        LandingScreenState.screenState(LandingScreenType.OnBoardLightningBasicInfo)
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
                            relayDataHandler.persistAuthorizationToken(onboardStep1.authorizationToken)
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

                        connectManagerRepository.createOwnerAccount(signupBasicInfoState.nickname)
                        navigateTo(LandingScreenType.Loading)

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

    private suspend fun listenToOwnerRegistered() {
        connectManagerRepository.connectionManagerState.collect {
            if (it is OwnerRegistrationState.OwnerRegistered) {
                LandingScreenState.screenState(LandingScreenType.OnBoardLightningProfilePicture)
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
            // TODO V2 Implement balance
//
//            val balance = loadResponse.value
//            val localBalance = balance.localBalance
//            val remoteBalance = balance.remoteBalance
//
//            setSignupBasicInfoState {
//                copy(
//                    balance = NodeBalanceAll(localBalance, remoteBalance)
//                )
//            }
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
        continueToSphinxOnYourPhone()
    }

    private fun joinDefaultTribe(tribeInfo: TribeDto) {
        scope.launch(dispatchers.mainImmediate) {
            tribeInfo.amount = tribeInfo.price_to_join

//            chatRepository.joinTribe(tribeInfo).collect { loadResponse ->
//                when (loadResponse) {
//                    LoadResponse.Loading -> {}
//
//                    is Response.Error -> {
//                        continueToSphinxOnYourPhone()
//                    }
//                    is Response.Success -> {
//                        continueToSphinxOnYourPhone()
//                    }
//                }
//            }
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
