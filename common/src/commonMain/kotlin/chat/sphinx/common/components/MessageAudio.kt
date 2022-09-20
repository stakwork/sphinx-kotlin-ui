package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import java.io.File
import javax.sound.sampled.AudioSystem

@Composable
fun MessageAudio(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    val localFilepath = chatMessage.message.messageMedia?.localFile

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.height(62.dp).padding(12.dp)
    ) {
        if (localFilepath != null) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        else {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
        }
        Box(modifier = Modifier.fillMaxWidth(0.4f).padding(start = 12.dp, end = 12.dp)
        ) {
            val slideValue = remember { mutableStateOf(0f) }
            Slider(
                value = slideValue.value,
                onValueChange = {
                    slideValue.value = it
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground,
                    thumbColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
//        if (chatMessage.message.messageMedia?.localFile != null ) {
//            chatMessage.message.messageMedia?.localFile?.let { path ->
//                val audioInputStream = AudioSystem.getAudioFileFormat(path.toFile())
//                val format = audioInputStream.format
//                val audioFileLength = audioInputStream.frameLength
//                val frameSize = format.frameSize
//                val frameRate = format.frameRate
//                val durationInSeconds = (audioFileLength / (frameSize * frameRate))
//                println("duration $durationInSeconds")
//            }
//        }

        Text("0.06", color = MaterialTheme.colorScheme.tertiary)
    }
}