package signUp

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize

@Composable
actual fun PinAndNameView() {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
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
//                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f).background(MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = imageResource(Res.drawable.ic_desktop_bg_dots),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(
                        "You are now on the \n lightening network",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
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
                        TextField("NickName") {}
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField("Set Pin (6 Digits)") {}
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField("Confirm Pin") {}
                    }
                    Column() {
//                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.height(35.dp).fillMaxWidth()) {
                            Button(
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.fillMaxWidth().height(35.dp),
                                onClick = {

                                }
                            ) {
                                androidx.compose.material.Text(
                                    text = "Continue",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.W400,
                                    fontFamily = Roboto
                                )
                            }
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.align(Alignment.CenterEnd).size(18.dp).padding(end = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                }
            }
        }
    }
}

@Composable
private fun TextField(headerName: String, onTextChange: (String) -> Unit) {
    val value = remember { mutableStateOf("") }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(headerName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground)
    }
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        shape = RoundedCornerShape(56.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(

            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textColor = MaterialTheme.colorScheme.tertiary,
        ),

        value = value.value,
        textStyle = LocalTextStyle.current.copy(
            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
            fontSize = 10.sp
        ),
        modifier = Modifier.fillMaxWidth().height(40.dp),
        onValueChange = {
            onTextChange(it)
            value.value = it
        },
        singleLine = true,
    )
}