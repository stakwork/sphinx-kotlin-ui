package chat.sphinx.common.components.landing

import androidx.compose.foundation.Image
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewUserScreen(
    invitationCodeText: String,
    invitationCodeErrorMessage: String? = null,
    onInvitationCodeTextChanged: (String) -> Unit,
    onSubmitInvitationCode: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(SolidColor(Color.Blue), alpha = 0.50f)
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
                .background(SolidColor(Color.Black), alpha = 0.50f)
        ) {
            // TODO: Back Button here...
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight()
            ) {
                TopAppBar(
                    title = { Spacer(modifier = Modifier.height(8.dp)) },

                    elevation = 0.dp,
                    backgroundColor = Color.Transparent,
                    navigationIcon = {
                        IconButton(onClick = {
                            LandingScreenState.screenState(LandingScreenType.LandingPage)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                        }
                    }
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {


                    Text(
                        text = "NEW USER",
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                    ) {
                        OutlinedTextField(
                            value = invitationCodeText,
                            modifier = Modifier
                                .weight(weight = 1F)
                                .onKeyEvent(onKeyUp(Key.Enter, onSubmitInvitationCode)),
                            onValueChange = onInvitationCodeTextChanged,
                            singleLine = true,
                            label = { Text(text = "Paste your invitation code") }
                        )
                    }

                    invitationCodeErrorMessage?.let {
                        Text(
                            text = invitationCodeErrorMessage,
                            color = Color.Red
                        )
                    }

                    Button(
                        onClick = {
                            // TODO: Handle exiting user...
                        }
                    ) {
                        Text(
                            text = "Submit"
                        )
                    }
                }
            }



        }
    }

}