package chat.sphinx.common.components.landing

import CommonButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.NewUserStore
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp
import org.intellij.lang.annotations.JdkConstants

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewUserScreen(
    newUserStore: NewUserStore,
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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = imageResource(Res.drawable.new_user_image),
                    contentDescription = "Sphinx new user graphic",
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Paste your invitation code into sphinx",
                    textAlign = TextAlign.Center
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
                IconButton({}){
                    Row(verticalAlignment = Alignment.CenterVertically, ){
                        IconButton(onClick = {
                            LandingScreenState.screenState(LandingScreenType.LandingPage)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                        Text("Back")
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {


                    Text(
                        text = "NEW USER",
                        textAlign = TextAlign.Center, color = Color.White, fontSize = 36.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                    ) {
                        OutlinedTextField(
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                            value = newUserStore.state.invitationCodeText,

                            modifier = Modifier
                                .weight(weight = 1F)
                                .onKeyEvent(
                                    onKeyUp(
                                        Key.Enter,
                                        newUserStore::onSubmitInvitationCode
                                    )
                                ),
                            onValueChange = newUserStore::onInvitationCodeTextChanged,
                            singleLine = true,
                            label = { Text(text = "Paste your invitation code") }
                        )
                    }

                    newUserStore.state.errorMessage?.let { invitationCodeErrorMessage ->
                        Text(
                            text = invitationCodeErrorMessage,
                            color = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                   CommonButton(text = "Submit",newUserStore::onSubmitInvitationCode)
                }
            }



        }
    }

    newUserStore.state.isProcessing?.let {

    }
}