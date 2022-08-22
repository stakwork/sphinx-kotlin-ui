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

    val topPadding = if (chatMessage.message.isPaidMessage && chatMessage.isSent) 44.dp else 12.dp

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

    if (message.isPaidPendingMessage) {
        PaidVideoOverlay()
    } else {
        Box(
            modifier = Modifier.height(250.dp).width(250.dp),
            contentAlignment = Alignment.Center
        )
        {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxSize()
            )
            {
                Image(
                    painter = imageResource(Res.drawable.landing_page_image),
                    contentDescription = "",
                )
            }
            Icon(
                Icons.Default.PlayCircle,
                contentDescription = "Play Button",
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
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
