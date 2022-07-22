package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import com.example.compose.place_holder_text

@Composable
fun PriceChip(
    chatViewModel: ChatViewModel?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(50),
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price:",
                fontSize = 13.sp,
                fontWeight = FontWeight.W700,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(4.dp)
            )
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = .4f), RoundedCornerShape(50))
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    singleLine = true,
                    modifier = Modifier
                        .width(width = 60.dp),
                    cursorBrush = SolidColor(Color.White),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.White
                    ),
                    onValueChange = {
                        if (chatViewModel != null) run {
                            if (it.toCharArray().isNotEmpty()) {
                                val re = Regex("[^0-9 ]")
                                val value = re.replace(it, "")
                                chatViewModel.onPriceTextChanged(value)
                            } else {
                                chatViewModel.onPriceTextChanged(it)
                            }
                        }
                    },
                    value = chatViewModel?.editMessageState?.price?.value?.toString() ?: ""
                )
            }
        }
    }
}