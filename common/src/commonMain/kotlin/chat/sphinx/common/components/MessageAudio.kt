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
import okio.Path

@Composable
fun MessageAudio(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    val message = chatMessage.message
    val messageMedia = message.messageMedia
    val localFilepath = messageMedia?.localFile
    val url = messageMedia?.url?.value ?: ""

    val audioState = chatMessage.audioState.value

    LaunchedEffect(url) {
        chatViewModel.downloadFileMedia(message, chatMessage.isSent)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(300.dp)
            .height(68.dp)
            .padding(start = 8.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            if (localFilepath != null) {
                if (localFilepath.isSupportedAudio()) {
                    chatViewModel.audioPlayer.loadAudio(chatMessage)
                }

                IconButton(
                    onClick = {
                        if (localFilepath.isSupportedAudio()) {
                            chatViewModel.audioPlayer.playAudio(chatMessage)
                        } else {
                            toast("Audio format not supported. Save the file to listen the audio message.")
                        }
                    }
                ) {
                    Icon(
                        if (audioState?.isPlaying == true) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
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
            modifier = Modifier.width(190.dp).padding(start = 8.dp)
        ) {
            Slider(
                value = audioState?.progress?.toFloat() ?: 0f,
                onValueChange = {},
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground,
                    thumbColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
        Box(
            modifier = Modifier.width(68.dp),
            contentAlignment = Alignment.Center
        ) {
            val seconds = (audioState?.length ?: 0) - (audioState?.currentTime ?: 0)
            Text(seconds.toAudioTimeFormat(), color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Int.toAudioTimeFormat(): String {
    val minutes = (this / 60)
    val seconds = (this % 60)

    val minutesString = if (minutes < 10) "0$minutes" else minutes
    val secondsString = if (seconds < 10) "0$seconds" else seconds

    return "$minutesString:$secondsString"
}

@Suppress("NOTHING_TO_INLINE")
inline fun Path.isSupportedAudio(): Boolean =
    (this.toString().contains(".wav") || this.toString().contains(".ogg") || this.toString().contains(".mp3"))
