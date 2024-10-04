package chat.sphinx.common.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import chat.sphinx.common.components.landing.*
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.SignUpViewModel
import theme.primary_green


@Composable
fun LandingScreen() {
    val signUpViewModel = remember { SignUpViewModel() }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (LandingScreenState.screenState()) {
            LandingScreenType.LandingPage -> {
                LandingUI()
            }
            LandingScreenType.NewUser -> {
                NewUserScreen(signUpViewModel)
            }
            LandingScreenType.RestoreUser -> {
                RestoreExistingUserScreen(signUpViewModel)
            }
            LandingScreenType.Loading -> {
                ConnectingDialog()
            }
            LandingScreenType.SignupLocked -> {
                OnBoardSignupLocked(signUpViewModel)
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
                OnBoardSphinxOnYourPhone(signUpViewModel)
            }
        }

        if (signUpViewModel.showSelectNetworkDialog.value) {
            SelectNetworkDialog(
                onDismiss = { signUpViewModel.showSelectNetworkDialog.value = false },
                onRegtestSelected = {
                    signUpViewModel.onNetworkTypeSelected(true)
                },
                onBitcoinSelected = {
                    signUpViewModel.onNetworkTypeSelected(false)
                }
            )
        }

        if (signUpViewModel.showMnemonicDialog.value) {
            MnemonicDialog(
                mnemonicWords = signUpViewModel.mnemonic.value,
                onDismiss = { signUpViewModel.closeMnemonicDialog()
                },
                onCopyToClipboard = {
                    signUpViewModel.toast("Mnemonic words copied to clipboard", color = primary_green)
                }
            )
        }
    }

}