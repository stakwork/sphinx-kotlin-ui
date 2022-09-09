package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.wrapper.PhotoUrl
import okio.Path

data class SignUpState(
    val messageFromFriend: String = "A message from your friend…",
    val friendPhotoUrl: PhotoUrl? = PhotoUrl("https://s3-alpha-sig.figma.com/img/fa29/05ee/d4461b47820b024258346c3a5fa45c9d?Expires=1663545600&Signature=FG4Yxg0WAd11Um72xuKVmBmGxdruQZnhKvLG3nJNLYZbEDXtU~B31aLe93tHDoL8Hb4IPnn6LoMZZuusTifSRbnPwNQPPEJ41oMFsej2DnNJsl-Ysf37nUxomSPpJwaLc6soniVL6-JKPxMDg~0DN8aJHamdmmDqMtYsacMkm2lPgne5NzRYxSq9u2opzD4Z-yW8qvrDAHEnNLXkMjfZDQNld~mZxMelr1By8Nt7CYVaOzV-gZAh~kT-oAIiW77jMM78G2iIZni07GWM1NmzlbJwbqY~AH3ktOzo0YTUjuAgFK9T~uLLxA9VXiKSfbLgoPWLhWoSokTfD-bkPTJ8Jw__&Key-Pair-Id=APKAINTVSUGEWH5XD5UA"),
    val friendName: String = "Tracey Walter",
    var signUpScreenState: MutableState<SignUpScreenState> = mutableStateOf(SignUpScreenState.BasicInfo),
    val nickname: String = "",
    val newPin: String = "",
    val confirmedPin: String = "",
    val basicInfoButtonEnabled: Boolean = false,
    var userPhotoFile: Path? = null


)

sealed class SignUpScreenState {
    object BasicInfo : SignUpScreenState()
    object ProfileImage : SignUpScreenState()
    object EndScreen : SignUpScreenState()
}