package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.*
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.toFileName
import okio.Path

class SignUpViewModel {

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

    //SIGNUP INVITER INFO STATE
    var signupInviterState: SignupInviterState by mutableStateOf(initialSignupInviterState())

    private fun initialSignupInviterState(): SignupInviterState = SignupInviterState()

    private inline fun setSignupInviterState(update: SignupInviterState.() -> SignupInviterState) {
        signupInviterState = signupInviterState.update()
    }

    //SIGNUP BASIC INFO STATE
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
        LandingScreenState.screenState(LandingScreenType.OnBoardMessage)
    }
}