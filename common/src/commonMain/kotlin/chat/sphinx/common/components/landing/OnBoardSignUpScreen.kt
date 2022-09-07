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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.utils.SphinxFonts
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.platform.imageResource
import okio.Path

import theme.md_theme_dark_onBackground

@Composable
fun OnBoardSignUpScreen() {
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
            OnBoardLightningScreen(isWelcome = false, isEndScreen = true)
        }
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            ProfileImage()
        }
    }
}

@Composable
fun SignUpScreen() {
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
            var nameText by remember { mutableStateOf("") }
            var setPinText by remember { mutableStateOf("") }
            var confirmPinText by remember { mutableStateOf("") }


            Spacer(modifier = Modifier.height(34.dp))
            TextField(
                value = nameText,
                textLabel = "Nickname",
                modifier = Modifier.fillMaxWidth(),
                isPin = false
            ) {
                nameText = it
            }
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = setPinText,
                textLabel = "Set PIN",
                modifier = Modifier.fillMaxWidth(),
                isPin = true
            ) {
                setPinText = it
            }
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = confirmPinText,
                textLabel = "Confirm PIN",
                modifier = Modifier.fillMaxWidth(),
                isPin = true
            ) {
                confirmPinText = it
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        Box(modifier = Modifier.height(48.dp).width(259.dp)) {
            CommonButton(text = "Continue", true, endIcon = Icons.Default.ArrowForward) {
                LandingScreenState.screenState(LandingScreenType.OnBoardSignUp)
            }
        }
    }
}

@Composable
fun ProfileImage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        BackButton()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(
            text = "Wayne Michaels",
            fontSize = 30.sp,
            color = md_theme_dark_onBackground,
            fontFamily = Roboto,
            fontWeight = FontWeight.W400,
        )
        Spacer(modifier = Modifier.height(64.dp))
        ProfileBox(null)

    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        UploadImage()
        Spacer(modifier = Modifier.height(18.dp))
        Box(modifier = Modifier.height(48.dp).width(259.dp)) {
            CommonButton(text = "Skip", true, endIcon = Icons.Default.ArrowForward) {
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
            focusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
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
private fun BackButton() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 28.dp, start = 20.dp),
    ) {
        Row(
            modifier = Modifier.clickable {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color.Gray)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = Roboto
            )
        }
    }
}

@Composable
private fun ProfileBox(path: Path?) {
    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth(),
            onDraw = {
                drawCircle(
                    md_theme_dark_onBackground, 164f,
                    Offset(size.width / 2, size.height / 2),
                    style = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                )
            })
        if (path != null) {
            PhotoFileImage(
                photoFilepath = path,
                modifier = Modifier.fillMaxSize().padding(12.dp).clip(CircleShape),
                effect = {}
            )
        } else {
            Image(
                modifier = Modifier.padding(12.dp),
                painter = imageResource(Res.drawable.profile_avatar),
                contentDescription = "avatar",
                contentScale = ContentScale.Inside
            )
        }
    }
}

@Composable
private fun UploadImage() {
    Box(modifier = Modifier.height(48.dp).width(259.dp)) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(23.dp),
            border = BorderStroke(1.dp, md_theme_dark_onBackground)
        ) {
            CommonButton(
                text = "Upload Image",
                enabled = true,
                endIcon = Icons.Default.CameraAlt,
                backgroundColor = MaterialTheme.colorScheme.surface,
            ) {}
        }
    }
}