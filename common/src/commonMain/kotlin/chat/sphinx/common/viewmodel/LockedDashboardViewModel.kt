package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardScreenState

class LockedDashboardViewModel: PinAuthenticationViewModel() {
    override fun onAuthenticationSucceed() {
        DashboardScreenState.screenState(DashboardScreenType.Unlocked)
    }
}