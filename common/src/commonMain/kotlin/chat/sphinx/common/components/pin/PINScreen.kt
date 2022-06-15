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
import androidx.compose.ui.text.TextStyle
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
import utils.AnimatedContainer

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PINScreen(
    pinHandlingViewModel: PINHandlingViewModel
) {
       Box(
           modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
       ) {
           Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {

//            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(MaterialTheme.colorScheme.background)){
//                IconButton(onClick = {
//                    LandingScreenState.screenState(LandingScreenType.LandingPage)
//                }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = MaterialTheme.colorScheme.tertiary)
//                }
//                Text("Back",color = MaterialTheme.colorScheme.tertiary)
//                Spacer(modifier = Modifier.weight(1f))
//            }

               Column(
                   verticalArrangement = Arrangement.Center,
                   horizontalAlignment = Alignment.CenterHorizontally,
                   modifier = Modifier.fillMaxHeight().background(color = MaterialTheme.colorScheme.background)
               ) {
                   // TODO: Have sphinx image...

                   AnimatedContainer(fromTopToBottom = 10) {
                       Icon(
                           Icons.Outlined.Lock,
                           "contentDescription",

                           modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.tertiary)
                   }
                   Spacer(modifier = Modifier.height(32.dp))
                   AnimatedContainer (fromTopToBottom = 30, delayTime = 20){
                       Image(
                           painter = imageResource(Res.drawable.enter_pin),
                           contentDescription = "Sphinx new user graphic",
                           modifier = Modifier.fillMaxWidth()
                       )
                   }
                   Spacer(modifier = Modifier.height(32.dp))
                   AnimatedContainer(fromBottomToTop = 10) {
                       Row(
                           modifier = Modifier
                               .fillMaxWidth(0.6f)
                       ) {
                           OutlinedTextField(
                               shape= RoundedCornerShape(56.dp),
                               textStyle= TextStyle(fontSize = 24.sp),
                               colors = TextFieldDefaults.outlinedTextFieldColors(

                                   focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                   backgroundColor=MaterialTheme.colorScheme.tertiary,
                                   unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),


                               value = pinHandlingViewModel.pinState.sphinxPIN,
                               modifier = Modifier
                                   .weight(weight = 1F)
                                   .onKeyEvent(onKeyUp(Key.Enter, pinHandlingViewModel::onSubmitPIN))
                                   .onKeyEvent(onKeyUp(Key.NumPadEnter, pinHandlingViewModel::onSubmitPIN)),
                               visualTransformation = PasswordVisualTransformation(),
                               onValueChange = pinHandlingViewModel::onPINTextChanged,
                               singleLine = true,
                               placeholder = {
                                   Text(
                                       text = "PIN to decrypt keys",
                                       fontFamily = SphinxFonts.montserratFamily
                                   )
                               }
                           )
                       }
                   }
                   Spacer(modifier = Modifier.height(32.dp))
                   pinHandlingViewModel.pinState.errorMessage?.let { errorMessage ->
                       Text(
                           text = errorMessage,
                           color = Color.Red
                       )
                   }

                   pinHandlingViewModel.pinState.infoMessage?.let { infoMessage ->
                       Text(
                           text = infoMessage,
                       )

                   }

               }
           }


       }




}