package chat.sphinx.common.components.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.store.ExistingUserStore
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PINScreen(
    existingUserStore: ExistingUserStore
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        // TODO: Have sphinx image...
        Text(
            text = "Enter PIN",
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(0.6f)
        ) {
            OutlinedTextField(
                value = existingUserStore.state.sphinxPIN,
                modifier = Modifier
                    .weight(weight = 1F)
                    .onKeyEvent(onKeyUp(Key.Enter, existingUserStore::onSubmitPIN)),
                onValueChange = existingUserStore::onPINTextChanged,
                singleLine = true,
                label = { Text(text = "PIN to decrypt keys") }
            )
        }

        existingUserStore.state.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }

        existingUserStore.state.infoMessage?.let { infoMessage ->
            Text(
                text = infoMessage,
//                color = Color.Red
            )
        }

        Button(
            onClick = existingUserStore::onSubmitPIN
        ) {
            Text(
                text = "Submit"
            )
        }
    }

}