package chat.sphinx.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import chat.sphinx.common.Res
import chat.sphinx.common.components.landing.ExistingUserScreen
import chat.sphinx.common.components.landing.LandingUI
import chat.sphinx.common.components.landing.NewUserScreen
import chat.sphinx.common.components.landing.RestoreFromKeychainScreen
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.store.ExistingUserStore
import chat.sphinx.common.store.NewUserStore
import chat.sphinx.common.store.RestoreFromKeystoreStore
import chat.sphinx.common.store.SphinxStore
import chat.sphinx.platform.imageResource

@Composable
fun LandingScreen() {
    val existingUserStore = remember { ExistingUserStore() }
    val restoreFromKeystoreStore = remember { RestoreFromKeystoreStore() }
    val newUserStore = remember { NewUserStore() }
    val newUserState = newUserStore.state

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Gray
    ) {
        when (LandingScreenState.screenState()) {
            LandingScreenType.LandingPage -> {
                LandingUI()
            }
            LandingScreenType.NewUser -> {
                NewUserScreen(
                    newUserState.invitationCodeText,
                    newUserState.errorMessage,
                    newUserStore::onInvitationCodeTextChanged,
                    newUserStore::onSubmitInvitationCode
                )
            }
            LandingScreenType.ExistingUser -> {
                ExistingUserScreen(existingUserStore)
            }
            LandingScreenType.RestoreFromKeystore -> {
                RestoreFromKeychainScreen(restoreFromKeystoreStore)
            }
        }
    }

}