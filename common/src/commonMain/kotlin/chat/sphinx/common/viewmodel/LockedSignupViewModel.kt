package chat.sphinx.common.viewmodel

import chat.sphinx.authentication.model.OnBoardStep
import chat.sphinx.authentication.model.OnBoardStepHandler
import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardScreenState
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.di.container.SphinxContainer
import kotlinx.coroutines.launch

class LockedSignupViewModel(private val signupViewModel: SignUpViewModel): PinAuthenticationViewModel() {

    private val onBoardStepHandler = OnBoardStepHandler()

    override fun onAuthenticationSucceed() {
        scope.launch(dispatchers.mainImmediate) {
            onBoardStepHandler.retrieveOnBoardStep()?.let { onBoardStep ->
                when (onBoardStep) {
                    is OnBoardStep.Step2_Name -> {
                        signupViewModel.setSignupBasicInfoState {
                            copy(
                                newPin = pinState.sphinxPIN,
                                confirmedPin = pinState.sphinxPIN,
                            )
                        }
                        LandingScreenState.screenState(LandingScreenType.OnBoardLightningBasicInfo)
                    }
                    is OnBoardStep.Step3_Picture -> {
                        LandingScreenState.screenState(LandingScreenType.OnBoardLightningProfilePicture)
                    }
                    is OnBoardStep.Step4_Ready -> {
                        signupViewModel.reloadAccountData()

                        LandingScreenState.screenState(LandingScreenType.OnBoardLightningReady)
                    }
                }
            }
        }
    }
}