package chat.sphinx.common.components.browser

import CommonButton
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.tribe.TopHeader
import chat.sphinx.common.components.tribe.TribeTextField
import chat.sphinx.common.state.ContentState
import chat.sphinx.response.LoadResponse
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import theme.primary_red
import utils.deduceMediaType

@Composable
actual fun AuthorizeBrowserDialog(onClose: () -> Unit, onSubmit: (String) -> Unit) {
    val regex = "-?\\d+(\\.\\d+)?".toRegex()
    val text = remember { mutableStateOf("") }
    Window(
        onCloseRequest = {
        },
        title = "Sphinx",
        undecorated = true,
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(300, 500)
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    onClose()
                }) {
                    androidx.compose.material.Icon(
                        Icons.Default.Close,
                        contentDescription = "close",
                        tint = primary_red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant)
                    .padding(vertical = 8.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Authorize", fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(32.dp))
                Text("http://www.google.co.in", fontSize = 18.sp, color = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.height(24.dp))
                Text("to withdraw up to", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    text.value,
                    onValueChange = {
                        if (it.toLongOrNull() != null)
                            text.value = it
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        cursorColor = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(40.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("sats before authorizing", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(32.dp))
                CommonButton("AUTHORIZE", fontWeight = FontWeight.W500, textButtonSize = 15.sp) {
                    onSubmit(text.value)
                    onClose()
                }
            }
        }

    }
}