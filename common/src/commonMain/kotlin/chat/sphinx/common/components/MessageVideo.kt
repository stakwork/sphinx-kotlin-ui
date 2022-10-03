package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlayCircleOutline
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.chat.retrieveRemoteMediaInputStream
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.message.isPaidPendingMessage
import chat.sphinx.wrapper.message.retrieveUrlAndMessageMedia
import kotlinx.coroutines.launch
import theme.primary_blue
import theme.primary_green


@Composable
fun MessageVideo(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier,
){
    val videoLoadError = rememberSaveable { mutableStateOf(false) }

    val message = chatMessage.message
    val messageMedia = message.messageMedia
    val localFilepath = messageMedia?.localFile
    val url = messageMedia?.url?.value ?: ""



    if (message.isPaidPendingMessage && chatMessage.isReceived) {
        PaidVideoOverlay(modifier)
    } else {
        LaunchedEffect(url) {
            if (localFilepath == null) {
                chatViewModel.downloadFileMedia(message, chatMessage.isSent)
            }
        }
        if (localFilepath != null) {
            Box(
                modifier = Modifier.height(250.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
            )
            {
                Image(
                    painter = imageResource(Res.drawable.ic_video_place_holder),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize()
                ) {}
                Icon(
                    Icons.Default.PlayCircleOutline,
                    contentDescription = "Play Button",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp).clickable {
                        toast("Video Player not implemented yet, save the file to watch the video")
                    }
                )
            }
        } else if (videoLoadError.value) {
            Image(
                painter = imageResource(Res.drawable.ic_received_image_not_available),
                contentDescription = "",
                modifier = Modifier.aspectRatio(1f)
            )
        } else {
           VideoLoadingView(modifier)
        }
    }
}

@Composable
fun VideoLoadingView(
    modifier: Modifier,
) {
    Box(
        modifier = modifier.aspectRatio(1f),
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
fun PaidVideoOverlay(
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = imageResource(Res.drawable.paid_image_blurred_placeholder),
            contentDescription = "",
            modifier = modifier.fillMaxWidth().aspectRatio(1f)
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
fun toast(
    message: String,
    color: Color = primary_green,
    delay: Long = 3000L
) {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    val sphinxNotificationManager = createSphinxNotificationManager()

    scope.launch(dispatchers.mainImmediate) {
        sphinxNotificationManager.toast(
            "Sphinx",
            message,
            color.value,
            delay
        )
    }
}
