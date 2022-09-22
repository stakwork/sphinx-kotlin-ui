package chat.sphinx.common.components.landing

import CommonButton
import OnBoardLightningScreen
import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.common.viewmodel.SignUpViewModel
import chat.sphinx.wrapper.PhotoUrl
import theme.md_theme_dark_onBackground

@Composable
fun OnBoardMessageScreen(viewModel: SignUpViewModel) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 86.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top

        ) {
            Text(
                text = "A message from your friend…",
                fontSize = 30.sp,
                color = md_theme_dark_onBackground,
                fontFamily = Roboto,
                fontWeight = FontWeight.ExtraLight,
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PhotoUrlImage(
            photoUrl = viewModel.signupInviterState.friendPhotoUrl,
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = viewModel.signupInviterState.friendName,
            fontSize = 30.sp,
            color = Color.White,
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
        )
        Spacer(modifier = Modifier.height(11.dp))
        Text(
            text = viewModel.signupInviterState.welcomeMessage,
            fontSize = 22.sp,
            color = md_theme_dark_onBackground,
            fontFamily = Roboto,
            fontWeight = FontWeight.Light,
        )
        Spacer(modifier = Modifier.height(44.dp))
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        Box(modifier = Modifier.height(48.dp).width(259.dp)) {
            CommonButton(text = "Get Started", true, endIcon = Icons.Default.ArrowForward) {
                LandingScreenState.screenState(LandingScreenType.OnBoardLightning)
            }
        }
    }
}
