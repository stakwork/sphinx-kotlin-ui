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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriceChip(
    text: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
//        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(32.dp),
//        border = BorderStroke(
//            width = 1.dp,
//            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
//        ),
//        modifier = modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer,)
    ) {
        Row(
            modifier = Modifier.padding(
                end = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Price:",
                style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 2.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )

            )

            var price = text
            TextField(
                value = price ?: "",
                singleLine = true,
                modifier = Modifier
                    .size(width = 40.dp, height = 20.dp).background(
                        androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .8f),
                    MaterialTheme.shapes.small,
                ).clip(RoundedCornerShape(10.dp)),
                onValueChange = { newValue -> price = newValue },
            )
        }
    }
}