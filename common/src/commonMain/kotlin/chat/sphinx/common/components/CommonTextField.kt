package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.unit.sp

@Composable
fun CommonTextField(placeholder: String) {
    val text = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val inputService = LocalTextInputService.current
    val focus = remember { mutableStateOf(false) }
    TextField(text.value,
        onValueChange = {
            text.value = it
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(

            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            backgroundColor = MaterialTheme.colorScheme.background,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
            cursorColor = MaterialTheme.colorScheme.tertiary, textColor = MaterialTheme.colorScheme.tertiary
        ),
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), fontSize = 12.sp) },
        modifier = Modifier.onFocusChanged {
            if (focus.value != it.isFocused) {
                focus.value = it.isFocused
                if (!it.isFocused) {
                    inputService?.hideSoftwareKeyboard()
                }
            }
        })
}