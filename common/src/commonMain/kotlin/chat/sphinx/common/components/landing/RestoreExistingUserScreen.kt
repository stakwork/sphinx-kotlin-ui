package chat.sphinx.common.components.landing

import CommonButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.RestoreExistingUserViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp
import utils.AnimatedContainer
import views.BackButton

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RestoreExistingUserScreen(
    restoreExistingUserViewModel: RestoreExistingUserViewModel
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            AnimatedContainer(fromBottomToTop = 20) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = imageResource(Res.drawable.existing_user_image),
                        contentDescription = "Existing user graphic",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
           AnimatedContainer(fromBottomToTop = 20) {
               Image(
                   painter = imageResource(Res.drawable.copy_paste_your_keys),
                   contentDescription = "Existing user graphic",
                   modifier = Modifier.fillMaxSize(0.4f).absoluteOffset(0.dp,250.dp)
               )
           }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // TODO: Back Button here...
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                BackButton()

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {


                    AnimatedContainer(fromTopToBottom = 20) {
                        Text(
                            text = "CONNECT",
                            textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary, fontSize = 32.sp, fontWeight = FontWeight.W700
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    AnimatedContainer(fromTopToBottom=20, delayTime = 20){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                        ) {
                            Box(
                                modifier = Modifier.height(48.dp).fillMaxWidth(), contentAlignment = Alignment.Center){

                                OutlinedTextField(
                                    shape= RoundedCornerShape(56.dp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(

                                        focusedBorderColor = MaterialTheme.colorScheme.background,
                                        backgroundColor=MaterialTheme.colorScheme.tertiary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)),
                                    value = restoreExistingUserViewModel.state.sphinxKeys,
                                    modifier = Modifier
                                        .onKeyEvent(onKeyUp(Key.Enter, restoreExistingUserViewModel::onSubmitKeys)),
                                    onValueChange = restoreExistingUserViewModel::onKeysTextChanged,
                                    singleLine = true,
                                    placeholder = { Text(text = "Paste your keys...") }
                                )
                            }
                        }
                    }

                    restoreExistingUserViewModel.state.errorMessage?.let { errorMessage ->
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedContainer(fromTopToBottom =20) {
                        Box(
                            modifier = Modifier.height(44.dp).fillMaxWidth(0.7f), contentAlignment = Alignment.Center){
                            CommonButton(text = "Submit",restoreExistingUserViewModel.state.sphinxKeys.isEmpty().not(),  restoreExistingUserViewModel::onSubmitKeys)
                            Row(modifier = Modifier.offset(x = 120.dp, y = 0.dp)) {
                                val textColor=if(restoreExistingUserViewModel.state.sphinxKeys.isEmpty().not())MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onTertiary
                                Icon(Icons.Filled.ArrowForward, "", modifier = Modifier.size(18.dp), tint = textColor)
                            }
                        }
                    }
                }
            }



        }
    }

}