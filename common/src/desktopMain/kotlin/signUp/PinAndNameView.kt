package signUp

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.Res
import chat.sphinx.common.components.CustomTextField
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.place_holder_text

@Composable
actual fun PinAndNameView() {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
    val hideCurrentWindow = remember { mutableStateOf(false) }
    if (hideCurrentWindow.value.not())
        Window(
            onCloseRequest = {},
            title = "Sphinx",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(800, 800)
            ),
            undecorated = true,
            icon = sphinxIcon,
        ) {
            Row(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f).background(MaterialTheme.colorScheme.background)
                ) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                        Image(
                            painter = imageResource(Res.drawable.ic_desktop_bg_dots),
                            modifier = Modifier.fillMaxWidth().fillMaxHeight().aspectRatio(1f),
                            contentDescription = null, contentScale = ContentScale.Crop
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(60.dp))
                        Text(
                            "You are now on the \n lightening network!",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W600
                        )

                    }
                    Box(
                        modifier = Modifier.size(25.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .align(Alignment.Center), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.size(7.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {

                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f).background(MaterialTheme.colorScheme.onSecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("GET STARTED", fontWeight = FontWeight.W600, color = MaterialTheme.colorScheme.tertiary)
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                            TextField("Nickname") {}
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField("Set PIN (6 Digits)", isPasswordField = true) {}
                            Spacer(modifier = Modifier.height(8.dp))
                            TextField("Confirm PIN", isPasswordField = true) {}
                        }
                        Button(
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.fillMaxWidth().height(35.dp),
                            onClick = {
                                hideCurrentWindow.value = true
                            }
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                                androidx.compose.material.Text(
                                    text = "Continue",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.W400,
                                    fontFamily = Roboto
                                )
                            }
                            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }

                        }

                    }
                }
            }
        }
    if (hideCurrentWindow.value)
        ProfileImageView()
}

@Composable
private fun TextField(headerName: String, isPasswordField: Boolean = false, onTextChange: (String) -> Unit) {
    val value = remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(headerName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground)
    }
    Spacer(modifier = Modifier.height(2.dp))
    val isFocus = remember { mutableStateOf(false) }
    Box {
        BasicTextField(
            modifier = Modifier.height(36.dp).clip(
                RoundedCornerShape(percent = 50)
            ).background(
                Color(0xFF001317),
            ).border(
                if (isFocus.value) BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.secondary
                ) else BorderStroke(2.dp, Color.Transparent), shape = RoundedCornerShape(50)
            ).onFocusChanged {
                isFocus.value = it.isFocused
            }.padding(horizontal = 16.dp, vertical = 4.dp),
            value = value.value,
            onValueChange = {
                value.value = it
            },

            singleLine = true,

            maxLines = 100,
            cursorBrush = SolidColor(androidx.compose.material.MaterialTheme.colors.primary),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
            ),
            visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) {
                        innerTextField()
                    }
                }
            }
        )
    }
}