package chat.sphinx.common.components.landing

import CommonButton

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.NewUserStore
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.onKeyUp
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.JdkConstants
import utils.AnimatedContainer
import views.BackButton

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
                .fillMaxHeight()
                .weight(1f)
                .background(MaterialTheme.colorScheme.secondary)
        ) { RightPortionNewUser() }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LeftPortionNewUser(newUserStore)
        }
    }

    newUserStore.state.isProcessing?.let {

    }
}

@Composable
fun RightPortionNewUser() {
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
fun LeftPortionNewUser(newUserStore: NewUserStore) {
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
                    text = "NEW USER",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = SphinxFonts.montserratFamily
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedContainer(fromTopToBottom = 20) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
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
                            value = newUserStore.state.invitationCodeText,

                            modifier = Modifier
                                .onKeyEvent(
                                    onKeyUp(
                                        Key.Enter,
                                        newUserStore::onSubmitInvitationCode
                                    )
                                ),
                            onValueChange = newUserStore::onInvitationCodeTextChanged,
                            singleLine = true,
                            placeholder = { Text(text = "Paste your invitation code") }
                        )
                    }
                }
            }

            newUserStore.state.errorMessage?.let { invitationCodeErrorMessage ->
                Text(
                    text = invitationCodeErrorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContainer(fromBottomToTop = 20) {
                Box(
                    modifier = Modifier.height(44.dp).fillMaxWidth(0.7f), contentAlignment = Alignment.Center
                ) {
                    CommonButton(
                        text = "Submit",
                        newUserStore.state.invitationCodeText.isNotEmpty(),
                        newUserStore::onSubmitInvitationCode
                    )
                    Row(modifier = Modifier.offset(x = 120.dp, y = 0.dp)) {
                        val textColor =
                            if (newUserStore.state.invitationCodeText.isNotEmpty()) androidx.compose.material3.MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onTertiary
                        Icon(Icons.Filled.ArrowForward, "", modifier = Modifier.size(18.dp), tint = textColor)
                    }
                }
            }
        }
    }
}