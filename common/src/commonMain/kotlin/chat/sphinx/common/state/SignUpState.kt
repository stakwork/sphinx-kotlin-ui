package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
    val friendPhotoUrl: PhotoUrl? = PhotoUrl("https://s3-alpha-sig.figma.com/img/fa29/05ee/d4461b47820b024258346c3a5fa45c9d?Expires=1663545600&Signature=FG4Yxg0WAd11Um72xuKVmBmGxdruQZnhKvLG3nJNLYZbEDXtU~B31aLe93tHDoL8Hb4IPnn6LoMZZuusTifSRbnPwNQPPEJ41oMFsej2DnNJsl-Ysf37nUxomSPpJwaLc6soniVL6-JKPxMDg~0DN8aJHamdmmDqMtYsacMkm2lPgne5NzRYxSq9u2opzD4Z-yW8qvrDAHEnNLXkMjfZDQNld~mZxMelr1By8Nt7CYVaOzV-gZAh~kT-oAIiW77jMM78G2iIZni07GWM1NmzlbJwbqY~AH3ktOzo0YTUjuAgFK9T~uLLxA9VXiKSfbLgoPWLhWoSokTfD-bkPTJ8Jw__&Key-Pair-Id=APKAINTVSUGEWH5XD5UA"),
    val friendName: String = "Tracey Walter"
)

data class SignupBasicInfoState(
    var lightningScreenState: LightningScreenState = LightningScreenState.Start,
    val nickname: String = "",
    val newPin: String = "",
    val confirmedPin: String = "",
    val basicInfoButtonEnabled: Boolean = false,
    val userPicture: AttachmentInfo? = null,
    val balance: NodeBalanceAll = NodeBalanceAll(Sat(0L), Sat(0L))
)


sealed class LightningScreenState {
    object Start : LightningScreenState()
    object BasicInfo : LightningScreenState()
    object ProfileImage : LightningScreenState()
    object EndScreen : LightningScreenState()
}