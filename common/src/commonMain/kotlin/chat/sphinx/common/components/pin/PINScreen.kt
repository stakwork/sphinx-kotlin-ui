package chat.sphinx.common.components.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.WelcomeScreen
import chat.sphinx.common.components.landing.ConnectingDialog
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.PINHandlingViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp
import chat.sphinx.utils.SphinxFonts
import com.example.compose.badge_red
import utils.AnimatedContainer

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PINScreen(
    pinHandlingViewModel: PINHandlingViewModel,
    pinScreenType: PINScreenType = PINScreenType.ENTER_PIN_TO_UNLOCK
) {
    Box(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                AnimatedContainer(fromTopToBottom = 10) {
                    Image(
                        painter = imageResource(Res.drawable.sphinx_logo),
                        contentDescription = "Sphinx Logo",
                        modifier = Modifier.height(80.dp),
                        contentScale = ContentScale.FillHeight
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContainer(fromTopToBottom = 30, delayTime = 20) {
                    Text(
                        text = "ENTER PIN",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = SphinxFonts.montserratFamily,
                        fontWeight = FontWeight.W500,
                        fontSize = 28.sp,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedContainer(fromBottomToTop = 10) {
                    Row(
                        modifier = Modifier.width(260.dp).height(68.dp)
                    ) {
                        OutlinedTextField(
                            shape = RoundedCornerShape(68.dp),
                            textStyle = TextStyle(fontSize = 24.sp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                backgroundColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(
                                    alpha = 0.8f
                                )
                            ),
                            value = pinHandlingViewModel.pinState.sphinxPIN,
                            modifier = Modifier
                                .weight(weight = 1F)
                                .onKeyEvent(onKeyUp(Key.Enter, pinHandlingViewModel::onSubmitPIN))
                                .onKeyEvent(
                                    onKeyUp(
                                        Key.NumPadEnter,
                                        pinHandlingViewModel::onSubmitPIN
                                    )
                                ),
                            visualTransformation = PasswordVisualTransformation(),
                            onValueChange = {
                                if (!pinHandlingViewModel.pinState.loading) {
                                    pinHandlingViewModel.onPINTextChanged(it)
                                }
                            },
                            singleLine = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.height(50.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (pinHandlingViewModel.pinState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.tertiary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        pinHandlingViewModel.pinState.errorMessage?.let { errorMessage ->
                            Text(
                                text = errorMessage,
                                color = badge_red
                            )
                        }
                    }
                }
                if (pinScreenType == PINScreenType.ENTER_PIN_TO_BACK_UP_YOUR_KEYS) {
                    Text(
                        "Enter your PIN to encrypt the keys. You will need it when restoring account on other device",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

enum class PINScreenType {
    ENTER_PIN_TO_UNLOCK,
    ENTER_PIN_TO_BACK_UP_YOUR_KEYS
}