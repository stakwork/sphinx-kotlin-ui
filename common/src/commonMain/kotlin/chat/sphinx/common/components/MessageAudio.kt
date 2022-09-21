package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val message = chatMessage.message
    val messageMedia = message.messageMedia
    val localFilepath = messageMedia?.localFile
    val url = messageMedia?.url?.value ?: ""

    LaunchedEffect(url) {
        chatViewModel.downloadFileMedia(message, chatMessage.isSent)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.height(68.dp).padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 12.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (localFilepath != null) {
                IconButton(
                    onClick = { toast("Please download the file, audio player not implemented yet") }
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }
            } else {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(0.4f).padding(start = 8.dp, end = 12.dp)
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
//        if (localFilepath != null) {
//            try {
//                val audioInputStream = AudioSystem.getAudioFileFormat(localFilepath.toFile())
//                val format = audioInputStream.format
//                val audioFileLength = audioInputStream.frameLength
//                val frameSize = format.frameSize
//                val frameRate = format.frameRate
//                val durationInSeconds = (audioFileLength / (frameSize * frameRate))
//            }
//            catch (e: Exception) {
//                println("Duration Error: $e ")
//            }
//        }

        Text("01:00", color = MaterialTheme.colorScheme.tertiary)
    }
}