package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.isPaidMessage
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.chat.retrieveRemoteMediaInputStream
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.message.isPaidPendingMessage
import chat.sphinx.wrapper.message.retrieveUrlAndMessageMedia


@Composable
fun MessageVideo(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
){
    val message = chatMessage.message
    val localFilepath = rememberSaveable { mutableStateOf(message.messageMedia?.localFile) }
    val videoLoadError = rememberSaveable { mutableStateOf(false) }
    val urlAndMessageMedia = message.retrieveUrlAndMessageMedia()
    val url = urlAndMessageMedia?.first
    val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler


    if (message.isPaidPendingMessage && chatMessage.isReceived) {
        PaidVideoOverlay()
    } else {
        LaunchedEffect(url) {
            if (localFilepath.value == null) {
                url?.let { mediaURL ->
                    try {
                        urlAndMessageMedia.second?.retrieveRemoteMediaInputStream(
                            mediaURL,
                            chatViewModel.memeServerTokenHandler,
                            chatViewModel.memeInputStreamHandler
                        )?.let { videoInputStream ->
                            mediaCacheHandler.createVideoFile("mp4").let { videoFilepath ->
                                videoFilepath.toFile().outputStream().use { fileOutputStream ->
                                    videoInputStream.copyTo(fileOutputStream)
                                    chatViewModel.messageRepository.messageMediaUpdateLocalFile(
                                        message,
                                        videoFilepath
                                    )
                                    localFilepath.value = videoFilepath
                                }
                            }
                        }
                    } catch (e: Exception) {
                        videoLoadError.value = true
                    }
                }
            }
        }
        if (localFilepath.value != null) {
            Box(
                modifier = Modifier.height(250.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                Image(
                    painter = imageResource(Res.drawable.existing_user_image),
                    contentDescription = "",
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize()
                )
                {}
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = "Play Button",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
        } else if (videoLoadError.value) {
            Image(
                painter = imageResource(Res.drawable.ic_received_image_not_available),
                contentDescription = "",
                modifier = Modifier.aspectRatio(1f)
            )
        } else
        {
           VideoLoadingView()
        }
    }
}

@Composable
fun VideoLoadingView(
) {
    Box(
        modifier = Modifier.height(250.dp).width(250.dp),
        contentAlignment = Alignment.Center
    ){
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier=Modifier.height(8.dp))
            Text("Loading/Decrypting...", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
fun PaidVideoOverlay() {
    Box(
        modifier = Modifier.height(250.dp).width(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = imageResource(Res.drawable.paid_image_blurred_placeholder),
            contentDescription = "",
            modifier = Modifier.height(250.dp).width(250.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Lock",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(29.dp).padding(2.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Pay to unlock this video",
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
