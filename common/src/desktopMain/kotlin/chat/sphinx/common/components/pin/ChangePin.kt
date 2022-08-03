package chat.sphinx.common.components.pin

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.ResetPinViewModel
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.badge_red

@Composable
fun ChangePin(
    viewModel: ResetPinViewModel,
    dashboardViewModel: DashboardViewModel
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "CHANGE PIN",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))
        TextField(
            value = viewModel.resetPinState.currentPin,
            textLabel = "Old PIN",
            modifier = Modifier.fillMaxWidth()
        ){
            viewModel.onCurrentPinChanged(it)
        }
        Spacer(modifier = Modifier.height(34.dp))
        TextField(
            value = viewModel.resetPinState.newPin,
            textLabel = "New PIN",
            modifier = Modifier.fillMaxWidth()
        ){
            viewModel.onNewPinChanged(it)
        }
        Spacer(modifier = Modifier.height(34.dp))
        TextField(
            value =viewModel.resetPinState.confirmedPin,
            textLabel = "Confirm New PIN",
            modifier = Modifier.fillMaxWidth()
        ){
            viewModel.onConfirmedNewPinChanged(it)
        }
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            Modifier.fillMaxWidth().height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.resetPinState.status is AuthenticateFlowResponse.Error ||
                viewModel.resetPinState.status is AuthenticateFlowResponse.WrongPin
            ) {
                Text(
                    text = viewModel.resetPinState.errorMessage ?: "There was an error, please try again later",
                    fontSize = 12.sp,
                    fontFamily = Roboto,
                    color = badge_red,
                )
            }
            if (viewModel.resetPinState.status is AuthenticateFlowResponse.Notify) {
                CircularProgressIndicator(
                    Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        }
        Box(
            Modifier.fillMaxWidth().height(48.dp)
        ) {
            Button(
                shape = RoundedCornerShape(23.dp),
                enabled = viewModel.resetPinState.buttonEnabled,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    disabledBackgroundColor = Color.White.copy(0.5f)
                ),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                onClick = {
                    viewModel.resetPassword()
                }
            ) {
                Text(
                    text = "Confirm",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.W600,
                    fontFamily = Roboto
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.align(
                    Alignment.CenterEnd
                ).size(30.dp).padding(end = 12.dp),
                tint = MaterialTheme.colorScheme.surface
            )
        }
    }

    if (viewModel.resetPinState.status is AuthenticateFlowResponse.Success){
        dashboardViewModel.toggleChangePinWindow(false)
    }
}

@Composable
private fun TextField(value: String, textLabel: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        shape = RoundedCornerShape(68.dp),
        modifier = modifier,
        label = {
            Text(
                text = textLabel,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = .7f),
                modifier = Modifier.padding(4.dp)
            )
        },
        textStyle = TextStyle(
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 15.sp,
            color = Color.White
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(
                alpha = 0.8f
            )
        ),
        value = value,
        visualTransformation = PasswordVisualTransformation(),
        onValueChange = {
            if(it.length <= 6){
                onValueChange(it)
            }
        },
        singleLine = true
    )
}