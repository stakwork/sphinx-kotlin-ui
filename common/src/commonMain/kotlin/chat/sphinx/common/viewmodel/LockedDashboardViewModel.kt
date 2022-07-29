package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardScreenState
import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LockedDashboardViewModel: PinAuthenticationViewModel() {
    override fun onAuthenticationSucceed() {
        DashboardScreenState.screenState(DashboardScreenType.Unlocked)
    }
}