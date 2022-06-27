package chat.sphinx.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriceChip(
    text: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(32.dp),
        modifier = modifier
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
                color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(
                    4.dp
                )

            )

            var price = text
            TextField(
                value = price ?: "",
                singleLine = true,
                modifier = Modifier
                    .size(width = 40.dp, height = 32.dp)
                    .background(
                        androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .8f),
                    MaterialTheme.shapes.small,
                ).clip(RoundedCornerShape(percent = 50)),
                colors = textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                onValueChange = { newValue -> price = newValue },
            )
        }
    }
}