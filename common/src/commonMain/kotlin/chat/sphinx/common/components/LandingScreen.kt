package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import chat.sphinx.common.components.landing.*
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.RestoreExistingUserViewModel
import chat.sphinx.common.viewmodel.SignUpViewModel


@Composable
fun LandingScreen() {
    val signUpViewModel = remember { SignUpViewModel() }
    val restoreExistingUserViewModel = remember { RestoreExistingUserViewModel() }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (LandingScreenState.screenState()) {
            LandingScreenType.LandingPage -> {
                LandingUI()
            }
            LandingScreenType.NewUser -> {
                NewUserScreen(signUpViewModel)
            }
            LandingScreenType.RestoreExistingUser -> {
                RestoreExistingUserScreen(restoreExistingUserViewModel)
            }
            LandingScreenType.ExistingUserPIN -> {
                ExistingUserPINScreen(restoreExistingUserViewModel)
            }
            LandingScreenType.Loading -> {
                ConnectingDialog()
            }
            LandingScreenType.RestoreExistingUserSuccess -> {
                WelcomeScreen()
            }
            LandingScreenType.OnBoardMessage -> {
                OnBoardMessageScreen(signUpViewModel)
            }
            LandingScreenType.OnBoardLightning,
            LandingScreenType.OnBoardLightningBasicInfo,
            LandingScreenType.OnBoardLightningProfilePicture,
            LandingScreenType.OnBoardLightningReady -> {
                OnBoardSignUpScreen(signUpViewModel)
            }
            LandingScreenType.OnBoardSphinxOnYourPhone -> {
                OnBoardSphinxOnYourPhone()
            }
        }
    }

}