package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color.Companion.Gray
import chat.sphinx.common.components.landing.*
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.ExistingUserStore
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
import chat.sphinx.common.viewmodel.NewUserStore
import chat.sphinx.common.viewmodel.RestoreFromKeystoreStore
import kotlinx.coroutines.FlowPreview
import utils.AnimatedContainer


@Composable
fun LandingScreen() {
    val existingUserStore = remember { ExistingUserStore() }
    val restoreFromKeystoreStore = remember { RestoreFromKeystoreStore() }
    val newUserStore = remember { NewUserStore() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Gray
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
            LandingScreenType.ExistingUser -> {
//                                val lockedDashboardViewModel = remember { LockedDashboardViewModel() }
//                PINScreen(lockedDashboardViewModel)

                    ExistingUserScreen(existingUserStore)

            }
            LandingScreenType.ExistingUserPIN -> {

                    ExistingUserPINScreen(existingUserStore)

            }
            LandingScreenType.RestoreFromKeystore -> {

                    RestoreFromKeychainScreen(restoreFromKeystoreStore)
            }
            LandingScreenType.Loading ->  ConnectingDialog()
        }

//        ConnectingDialog()
    }



}