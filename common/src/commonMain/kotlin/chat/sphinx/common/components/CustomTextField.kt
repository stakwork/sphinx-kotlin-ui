package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import theme.place_holder_text

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = "Placeholder",
    value: String,
    color: Color? = null,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
    singleLine: Boolean = true,
    maxLines: Int = 4,
) {
    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        maxLines = maxLines,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            color = color ?: place_holder_text
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty())
                        Text(
                            placeholderText,
                            style = LocalTextStyle.current.copy(
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha = .7f),
                                fontSize = fontSize
                            )
                        )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}