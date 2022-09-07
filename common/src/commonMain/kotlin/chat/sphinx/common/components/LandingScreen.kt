package chat.sphinx.common.components

import OnBoardLightningScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import chat.sphinx.common.components.landing.*
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.RestoreExistingUserViewModel
import chat.sphinx.common.viewmodel.NewUserStore
import chat.sphinx.common.viewmodel.RestoreFromKeystoreStore


@Composable
fun LandingScreen() {

    val restoreExistingUserViewModel = remember { RestoreExistingUserViewModel() }
    val restoreFromKeystoreStore = remember { RestoreFromKeystoreStore() }
    val newUserStore = remember { NewUserStore() }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {

        when (LandingScreenState.screenState()) {
            LandingScreenType.LandingPage -> {
                LandingUI()
            }
            LandingScreenType.NewUser -> {
                NewUserScreen(newUserStore)
            }
            LandingScreenType.RestoreExistingUser -> {
                RestoreExistingUserScreen(restoreExistingUserViewModel)
            }
            LandingScreenType.ExistingUserPIN -> {
                ExistingUserPINScreen(restoreExistingUserViewModel)
            }
            LandingScreenType.RestoreFromKeystore -> {
                RestoreFromKeychainScreen(restoreFromKeystoreStore)
            }
            LandingScreenType.Loading -> {
                ConnectingDialog()
            }
            LandingScreenType.RestoreExistingUserSuccess -> {
                WelcomeScreen()
            }
            LandingScreenType.OnBoardMessage -> {
                OnBoardMessageScreen()
            }
            LandingScreenType.OnBoardLightning -> {
                OnBoardLightningScreen(isWelcome = true, isEndScreen = false)
            }
            LandingScreenType.OnBoardSignUp -> {
                OnBoardSignUpScreen()
            }

        }
    }



}