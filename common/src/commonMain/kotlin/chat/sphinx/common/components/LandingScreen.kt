package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color.Companion.Gray
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
//                WelcomeScreen()
//                val lockedDashboardViewModel = remember { LockedDashboardViewModel() }
                LandingUI()

            }
            LandingScreenType.NewUser -> {
                NewUserScreen(
                    newUserStore
                )
            }
            LandingScreenType.RestoreExistingUser -> {
//                                val lockedDashboardViewModel = remember { LockedDashboardViewModel() }
//                PINScreen(lockedDashboardViewModel)

                RestoreExistingUserScreen(restoreExistingUserViewModel)
//                if(restoreExistingUserViewModel.state.isLoading==true){
//                    ConnectingDialog()
//                } else if(restoreExistingUserViewModel.state.infoMessage=="success"){
//                    WelcomeScreen()
//                } else {
//
//                }
            }
            LandingScreenType.ExistingUserPIN -> {
                ExistingUserPINScreen(restoreExistingUserViewModel)
            }
            LandingScreenType.RestoreFromKeystore -> {
                RestoreFromKeychainScreen(restoreFromKeystoreStore)
            }
            LandingScreenType.Loading ->  ConnectingDialog()
            LandingScreenType.RestoreExistingUserSuccess -> WelcomeScreen()
        }

//        ConnectingDialog()
    }



}