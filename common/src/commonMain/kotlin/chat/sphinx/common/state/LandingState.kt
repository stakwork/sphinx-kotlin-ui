package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class LandingScreenType {
    LandingPage,
    NewUser,
    ExistingUser,
    RestoreFromKeystore
}

object LandingScreenState {
    private var screen: MutableState<LandingScreenType> = mutableStateOf(LandingScreenType.LandingPage)

    fun screenState() : LandingScreenType {
        return screen.value
    }

    fun screenState(state: LandingScreenType) {
        screen.value = state
    }
}