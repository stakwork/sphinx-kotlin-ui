package chat.sphinx.common.components.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import views.BackButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExistingUserPINScreen() {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SolidColor(MaterialTheme.colorScheme.error), alpha = 0.50f)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                ) {
                    BackButton {
                        LandingScreenState.screenState(LandingScreenType.LandingPage)
                    }
                    Text("Back",color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.weight(1f))
                }

//                PINScreen(restoreExistingUserViewModel)
            }
        }
    }

}