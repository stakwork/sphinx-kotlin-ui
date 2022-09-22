package views

import Roboto
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 28.dp, start = 20.dp),
    ) {
        Row(
            modifier = Modifier.clickable(
                onClick = {
                    onClick.invoke()
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color.Gray)
            Spacer(modifier = Modifier.width(14.dp))
            androidx.compose.material3.Text(
                text = "Back",
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = Roboto
            )
        }
    }
}