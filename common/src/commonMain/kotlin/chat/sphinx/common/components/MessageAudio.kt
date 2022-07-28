package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel

@Composable
fun MessageAudio(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
        Box(modifier = Modifier.fillMaxWidth(0.8f)) {
            val slideValue = remember { mutableStateOf(0.1f) }
            Slider(
                value = slideValue.value,
                onValueChange = {
                    slideValue.value = it
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.onBackground,
                    thumbColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
        Text("0:06", color = MaterialTheme.colorScheme.tertiary)
    }
}