package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class ScreenType {
    SplashScreen,
    DashboardScreen,
    LandingScreen
}

object AppState {
    private var screen: MutableState<ScreenType> = mutableStateOf(ScreenType.SplashScreen)

    fun screenState() : ScreenType {
        return screen.value
    }

    fun screenState(state: ScreenType) {
        screen.value = state
    }
}