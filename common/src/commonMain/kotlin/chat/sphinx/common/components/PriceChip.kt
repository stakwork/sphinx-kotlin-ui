package chat.sphinx.common.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PriceChip(
    text: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Green,
        contentColor = Color.White,
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.Green
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(
                end = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price:",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
            )

            var price = text
            TextField(
                value = price ?: "",
                singleLine = true,
                modifier = Modifier
                    .size(20.dp),
                onValueChange = { newValue -> price = newValue },
            )
        }
    }
}