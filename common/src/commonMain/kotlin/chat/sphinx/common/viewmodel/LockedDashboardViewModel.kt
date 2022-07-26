package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardState
import chat.sphinx.common.viewmodel.PinAuthenticationViewModel

class LockedDashboardViewModel: PinAuthenticationViewModel() {
    override fun onAuthenticationSucceed() {
        DashboardState.screenState(DashboardScreenType.Unlocked)
    }


}