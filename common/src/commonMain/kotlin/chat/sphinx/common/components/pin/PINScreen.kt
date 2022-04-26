package chat.sphinx.common.components.pin

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import chat.sphinx.common.viewmodel.PINHandlingViewModel
import chat.sphinx.utils.onKeyUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PINScreen(
    pinHandlingViewModel: PINHandlingViewModel
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
                value = pinHandlingViewModel.pinState.sphinxPIN,
                modifier = Modifier
                    .weight(weight = 1F)
                    .onKeyEvent(onKeyUp(Key.Enter, pinHandlingViewModel::onSubmitPIN))
                    .onKeyEvent(onKeyUp(Key.NumPadEnter, pinHandlingViewModel::onSubmitPIN)),
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = pinHandlingViewModel::onPINTextChanged,
                singleLine = true,
                label = { Text(text = "PIN to decrypt keys") }
            )
        }

        pinHandlingViewModel.pinState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }

        pinHandlingViewModel.pinState.infoMessage?.let { infoMessage ->
            Text(
                text = infoMessage,
//                color = Color.Red
            )
        }

        Button(
            onClick = pinHandlingViewModel::onSubmitPIN
        ) {
            Text(
                text = "Submit"
            )
        }
    }

}