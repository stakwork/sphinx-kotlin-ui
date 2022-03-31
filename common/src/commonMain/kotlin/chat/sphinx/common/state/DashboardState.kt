package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

enum class DashboardScreenType {
    Locked,
    Unlocked
}

object DashboardState {
    private var screen: MutableState<DashboardScreenType> = mutableStateOf(DashboardScreenType.Locked)

    fun screenState() : DashboardScreenType {
        return screen.value
    }

    fun screenState(state: DashboardScreenType) {
        screen.value = state
    }
}