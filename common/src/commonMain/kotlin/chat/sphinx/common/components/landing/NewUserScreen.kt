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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.SignUpViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.onKeyUp
import theme.badge_red
import utils.AnimatedContainer
import views.BackButton

@OptIn(ExperimentalComposeUiApi::class)

@Composable
fun NewUserScreen(
    signUpViewModel: SignUpViewModel
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(MaterialTheme.colorScheme.secondary)
        ) { LeftPortionNewUser() }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RightPortionNewUser(signUpViewModel)
        }
    }
}

@Composable
fun LeftPortionNewUser() {
    Box() {
        AnimatedContainer(fromTopToBottom = 20) {
            Image(
                painter = imageResource(Res.drawable.new_user_image),
                contentDescription = "Sphinx new user graphic",
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedContainer(fromBottomToTop = 20) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(230.dp))
                Image(
                    painter = imageResource(Res.drawable.paste_your_invitation),
                    contentDescription = "Sphinx new user graphic",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RightPortionNewUser(
    signUpViewModel: SignUpViewModel
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        BackButton {
            LandingScreenState.screenState(LandingScreenType.LandingPage)
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            AnimatedContainer(fromTopToBottom = 20) {
                Text(
                    text = "NEW USER",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = SphinxFonts.montserratFamily
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedContainer(fromTopToBottom = 20) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.65f)
                ) {
                    Box(
                        modifier = Modifier.height(56.dp).fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {

                        OutlinedTextField(
                            shape = RoundedCornerShape(56.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(

                                focusedBorderColor = MaterialTheme.colorScheme.background,
                                backgroundColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                            ),
                            value = signUpViewModel.signupCodeState.invitationCodeText,

                            modifier = Modifier.fillMaxWidth()
                                .onKeyEvent(
                                    onKeyUp(
                                        Key.Enter,
                                        signUpViewModel::onSubmitInvitationCode
                                    )
                                ),
                            onValueChange = signUpViewModel::onInvitationCodeTextChanged,
                            singleLine = true,
                            placeholder = { Text(text = "Paste your invitation code") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContainer(fromBottomToTop = 20) {
                Box(
                    modifier = Modifier.height(44.dp).fillMaxWidth(0.65f), contentAlignment = Alignment.Center
                ) {
                    CommonButton(
                        text = "Submit",
                        enabled = signUpViewModel.signupCodeState.invitationCodeText.isNotEmpty(),
                        callback = signUpViewModel::onSubmitInvitationCode
                    )
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(16.dp, 0.dp)
                    ) {
                        val textColor =
                            if (signUpViewModel.signupCodeState.invitationCodeText.isNotEmpty()) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onTertiary
                        Icon(
                            Icons.Filled.ArrowForward,
                            "arrow",
                            modifier = Modifier.size(18.dp),
                            tint = textColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.height(20.dp), contentAlignment = Alignment.Center
            ) {
                signUpViewModel.signupCodeState.errorMessage?.let { invitationCodeErrorMessage ->
                    Text(
                        text = invitationCodeErrorMessage,
                        color = badge_red
                    )
                }
            }
        }
    }
}