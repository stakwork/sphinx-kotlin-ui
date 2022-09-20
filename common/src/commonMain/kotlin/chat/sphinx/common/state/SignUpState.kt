package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.authentication.model.OnBoardStep
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.lightning.NodeBalanceAll
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.lightning.toSat
import okio.Path

data class SignupCodeState(
    val invitationCodeText: String = "",
    val errorMessage: String? = null,
)

data class SignupInviterState(
    val friendPhotoUrl: PhotoUrl? = null,
    val friendName: String = "Sphinx Support",
    val welcomeMessage: String = "Welcome to Sphinx"
)

data class SignupBasicInfoState(
    val nickname: String = "",
    val newPin: String = "",
    val confirmedPin: String = "",
    val basicInfoButtonEnabled: Boolean = false,
    val userPicture: MutableState<AttachmentInfo?> = mutableStateOf(null),
    val balance: NodeBalanceAll = NodeBalanceAll(Sat(0L), Sat(0L)),
    val onboardStep: OnBoardStep? = null,
    val showLoading: Boolean = false
)