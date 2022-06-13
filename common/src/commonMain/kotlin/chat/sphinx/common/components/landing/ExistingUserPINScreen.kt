package chat.sphinx.common.components.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.ExistingUserStore
import views.BackButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ExistingUserPINScreen(
    existingUserStore: ExistingUserStore
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SolidColor(Color.Black), alpha = 0.50f)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background)){
                BackButton()
                Text("Back",color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.weight(1f))
            }

                PINScreen(existingUserStore)
            }

        }
    }

}