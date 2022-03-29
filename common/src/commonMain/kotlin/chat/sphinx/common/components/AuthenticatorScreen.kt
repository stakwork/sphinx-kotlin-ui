package chat.sphinx.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthenticatorScreen(
    text: String,
    errorMessage: String? = null,
    onTextChanged: (String) -> Unit,
    onSubmitPin: () -> Unit
) {
//    val authenticationViewModel = AuthenticationViewModel()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(SolidColor(Color.Black), alpha = 0.50f)
    ) {

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            contentAlignment = Alignment.Center,

            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(SolidColor(Color.Blue), alpha = 0.50f)
        ) {
            Image(
                painter = imageResource(Res.drawable.sphinx_logo),
                contentDescription = "Sphinx Logo",
                modifier = Modifier.fillMaxSize(0.2f),
            )
        }

        Text(
            text = "Enter Pin"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(0.6f)
        ) {
            OutlinedTextField(
                value = text,
                modifier = Modifier
                    .weight(weight = 1F)
                    .onKeyEvent(onKeyUp(Key.Enter, onSubmitPin)),
                onValueChange = onTextChanged,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
//            label = { Text(text = "Add a todo") }
            )
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = errorMessage,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
    }

    // TODO: Add pin dialogue...
}