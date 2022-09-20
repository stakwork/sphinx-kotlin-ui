package chat.sphinx.common.components.landing

import CommonButton
import OnBoardLightningScreen
import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.utils.SphinxFonts
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.*
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.viewmodel.SignUpViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.signup.SignupUploadImageButton
import chat.sphinx.wrapper.lightning.asFormattedString
import okio.Path
import theme.md_theme_dark_onBackground
import views.BackButton

@Composable
fun OnBoardSignUpScreen(viewModel: SignUpViewModel) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(MaterialTheme.colorScheme.secondary)

        ) {
            OnBoardLightningScreen(viewModel)
        }
        if (LandingScreenState.screenState() != LandingScreenType.OnBoardLightning) {
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when (LandingScreenState.screenState()) {
                    LandingScreenType.OnBoardLightningBasicInfo -> {
                        BasicInfoScreen(viewModel)
                    }
                    LandingScreenType.OnBoardLightningProfilePicture -> {
                        ProfileImage(viewModel)
                    }
                    LandingScreenType.OnBoardLightningReady -> {
                        EndScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BasicInfoScreen(viewModel: SignUpViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "GET STARTED",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 22.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(top = 74.dp),
                fontFamily = SphinxFonts.montserratFamily
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 77.dp, top = 62.dp, end = 77.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(34.dp))
            TextField(
                value = viewModel.signupBasicInfoState.nickname,
                textLabel = "Nickname",
                modifier = Modifier.fillMaxWidth(),
                isPin = false
            ) {
                viewModel.onNicknameChanged(it)
            }
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = viewModel.signupBasicInfoState.newPin,
                textLabel = "Set PIN",
                modifier = Modifier.fillMaxWidth(),
                isPin = true
            ) {
                viewModel.onNewPinChanged(it)
            }
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = viewModel.signupBasicInfoState.confirmedPin,
                textLabel = "Confirm PIN",
                modifier = Modifier.fillMaxWidth(),
                isPin = true
            ) {
                viewModel.onConfirmedPinChanged(it)
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        if (viewModel.signupBasicInfoState.showLoading) {
            CircularProgressIndicator(
                Modifier.padding(20.dp).size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
        Box(modifier = Modifier.height(48.dp).width(259.dp)) {
            CommonButton(
                text = "Continue",
                endIcon = Icons.Default.ArrowForward,
                enabled = viewModel.signupBasicInfoState.basicInfoButtonEnabled
            ) {
                viewModel.onSubmitNicknameAndPin()
            }
        }
    }
}


@Composable
fun ProfileImage(viewModel: SignUpViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        BackButton {
            viewModel.navigateTo(LandingScreenType.OnBoardLightningBasicInfo)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(
            text = viewModel.signupBasicInfoState.nickname,
            fontSize = 30.sp,
            color = md_theme_dark_onBackground,
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
        )
        Spacer(modifier = Modifier.height(64.dp))
        ProfileBox(viewModel.signupBasicInfoState.userPicture.value?.filePath, viewModel)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        SignupUploadImageButton(viewModel)
        Spacer(modifier = Modifier.height(18.dp))
        Box(modifier = Modifier.height(48.dp).width(259.dp)) {
            CommonButton(
                text = if (viewModel.signupBasicInfoState.userPicture == null) "Skip" else "Continue",
                true,
                endIcon = Icons.Default.ArrowForward
            ) {
                viewModel.updateProfilePic()
            }
        }
    }
}

@Composable
fun EndScreen(viewModel: SignUpViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 132.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = " YOU'RE READY\n" +
                    "TO USE SPHINX!",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 22.sp,
            fontWeight = FontWeight.W600,
            modifier = Modifier.padding(top = 74.dp),
            fontFamily = SphinxFonts.montserratFamily
        )
        Spacer(modifier = Modifier.height(86.dp))

        Text(
            text = "You can send messages,",
            fontSize = 18.sp,
            color = Color.White,
            fontFamily = Roboto,
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "spend ${viewModel.signupBasicInfoState.balance.localBalance.asFormattedString(' ', true)},",
            fontSize = 18.sp,
            color = Color.White,
            fontFamily = Roboto,
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "or receive up to ${
                viewModel.signupBasicInfoState.balance.remoteBalance.asFormattedString(
                    ' ',
                    true
                )
            }.",
            fontSize = 18.sp,
            color = Color.White,
            fontFamily = Roboto,
            fontWeight = FontWeight.Light,
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        if (viewModel.signupBasicInfoState.showLoading) {
            CircularProgressIndicator(
                Modifier.padding(20.dp).size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
        Box(modifier = Modifier.height(48.dp).width(259.dp)) {
            CommonButton(text = "Finish", true, endIcon = Icons.Default.ArrowForward) {
                viewModel.onReadySubmit()
            }
        }
    }
}

@Composable
private fun TextField(
    value: String,
    textLabel: String,
    modifier: Modifier = Modifier,
    isPin: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        shape = RoundedCornerShape(68.dp),
        modifier = modifier,
        placeholder = {
            Text(
                text = textLabel,
                fontSize = 14.sp,
                color = md_theme_dark_onBackground,
                modifier = Modifier.padding(start = 8.dp),
                fontFamily = Roboto,
            )
        },
        textStyle = TextStyle(
            fontSize = if (isPin) 24.sp else 16.sp,
            textAlign = TextAlign.Center,
            letterSpacing = if (isPin) 15.sp else 1.sp,
            color = Color.White,
            fontFamily = Roboto,
            fontWeight = FontWeight.Light
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            cursorColor = md_theme_dark_onBackground
        ),
        value = value,
        visualTransformation = if (isPin) PasswordVisualTransformation() else VisualTransformation.None,
        onValueChange = {
            if (isPin) {
                if (it.length <= 6) {
                    onValueChange(it)
                }
            } else {
                onValueChange(it)
            }
        },
        singleLine = true,
    )
}

@Composable
private fun ProfileBox(path: Path?, viewModel: SignUpViewModel) {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth(),
            onDraw = {
                drawCircle(
                    md_theme_dark_onBackground, 164f,
                    Offset(size.width / 2, size.height / 2),
                    style = Stroke(
                        width = 4f
                    ),
                )
            })
        if (viewModel.signupBasicInfoState.showLoading) {
            CircularProgressIndicator(
                Modifier.fillMaxSize(),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
        if (path != null) {
            PhotoFileImage(
                photoFilepath = path,
                modifier = Modifier.fillMaxSize().padding(12.dp).clip(CircleShape),
                effect = {},
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                painter = imageResource(Res.drawable.profile_avatar),
                contentDescription = "avatar",
                contentScale = ContentScale.Inside
            )
        }
    }
}