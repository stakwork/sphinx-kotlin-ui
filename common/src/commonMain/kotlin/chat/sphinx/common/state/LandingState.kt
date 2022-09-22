package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.common.components.landing.OnBoardMessageScreen
import chat.sphinx.common.components.landing.OnBoardSignUpScreen
import chat.sphinx.common.components.landing.OnBoardSphinxOnYourPhone

enum class LandingScreenType {
    LandingPage,
    NewUser,
    RestoreExistingUser,
    RestoreExistingUserSuccess,
    ExistingUserPIN,
    Loading,
    SignupLocked,
    OnBoardMessage,
    OnBoardLightning,
    OnBoardLightningBasicInfo,
    OnBoardLightningProfilePicture,
    OnBoardLightningReady,
    OnBoardSphinxOnYourPhone
}

object LandingScreenState {
    private var screen: MutableState<LandingScreenType> = mutableStateOf(LandingScreenType.LandingPage)

    fun screenState(): LandingScreenType {
        return screen.value
    }

    fun screenState(state: LandingScreenType) {
        screen.value = state
    }

    fun isUnlockedSignup(): Boolean {
        return (
            screenState() == LandingScreenType.OnBoardLightning ||
            screenState() == LandingScreenType.OnBoardLightningBasicInfo ||
            screenState() == LandingScreenType.OnBoardLightningProfilePicture ||
            screenState() == LandingScreenType.OnBoardLightningReady ||
            screenState() == LandingScreenType.OnBoardSphinxOnYourPhone
        )
    }
}