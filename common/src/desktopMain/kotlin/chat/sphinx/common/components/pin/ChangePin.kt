package chat.sphinx.common.components.pin

import CommonButton
import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.profile.onTapClose
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
import chat.sphinx.common.viewmodel.ResetPinViewModel
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.utils.onKeyUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChangePin(resetPinViewModel: ResetPinViewModel, dashboardViewModel: DashboardViewModel) {
    Window(
        onCloseRequest = { dashboardViewModel.toggleChangePinWindow(false) },
        title = "Sphinx",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(400, 500)
        ),
    ) {
        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
                .fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "STANDARD PIN",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = resetPinViewModel.pinState.sphinxPIN,
                textLabel = "Old PIN"
            ){
                resetPinViewModel.onPINTextChanged(it)
            }
            Spacer(modifier = Modifier.height(34.dp))
            TextField(
                value = resetPinViewModel.newPinState.newPin,
                textLabel = "New PIN"){
                resetPinViewModel.setNewPin(it)
            }
            Spacer(modifier = Modifier.height(34.dp))
            TextField(
                value =resetPinViewModel.newPinState.confirmedPin,
                textLabel = "Confirm New PIN"){
                resetPinViewModel.setConfirmedNewPin(it)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Box(modifier = Modifier.fillMaxWidth(.75f)) {

                Button(
                    shape = RoundedCornerShape(23.dp),
//        enabled = enabled?:true,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    onClick = {

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

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TextField(value: String, textLabel: String, modifier: Modifier = Modifier, onValueChange: (String) -> Unit) {
    val maxLength = 6
    OutlinedTextField(
        shape = RoundedCornerShape(68.dp),
        modifier = modifier,
        label = { Text(
            text = textLabel,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = .7f),
            modifier = Modifier.padding(4.dp)
        ) },
        textStyle = TextStyle(fontSize = 24.sp, color = Color.White),
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
            if(it.length <= maxLength){
            onValueChange(it)
            }
        },
        singleLine = true
    )
}