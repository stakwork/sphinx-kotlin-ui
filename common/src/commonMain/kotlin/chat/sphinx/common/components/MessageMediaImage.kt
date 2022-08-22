package chat.sphinx.common.components


import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.fullScreenImageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.retrieveRemoteMediaInputStream
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.MessageMedia

@Composable
fun MessageMediaImage(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    MessageMediaImage(
        chatMessage.message,
        chatViewModel,
        modifier,
        false,
        chatMessage.isReceived,
        ContentScale.Inside
    )
}

@Composable
fun MessageMediaImage(
    message: Message,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier,
    isReplyView: Boolean = false,
    isReceived: Boolean = false,
    contentScale: ContentScale = ContentScale.FillWidth
) {
    val localFilepath = rememberSaveable { mutableStateOf(message.messageMedia?.localFile) }
    val imageLoadError = rememberSaveable { mutableStateOf(false) }
    val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler

    val urlAndMessageMedia = message.retrieveUrlAndMessageMedia()
    val url = urlAndMessageMedia?.first

    if (
        message.isPaidPendingMessage && isReceived
    ) {
        PaidVideoOverlay(modifier, isReplyView)
    } else {
        LaunchedEffect(url) {
            if (localFilepath.value == null) {
                url?.let { mediaURL ->
                    try {
                        urlAndMessageMedia.second?.retrieveRemoteMediaInputStream(
                            mediaURL,
                            chatViewModel.memeServerTokenHandler,
                            chatViewModel.memeInputStreamHandler
                        )?.let { imageInputStream ->
                            mediaCacheHandler.createImageFile("jpg").let { imageFilepath ->
                                imageFilepath.toFile().outputStream().use { fileOutputStream ->
                                    imageInputStream.copyTo(fileOutputStream)

                                    chatViewModel.messageRepository.messageMediaUpdateLocalFile(
                                        message,
                                        imageFilepath
                                    )
                                    localFilepath.value = imageFilepath
                                }
                            }
                        }
                    } catch (e: Exception) {
                        imageLoadError.value = true
                    }
                }
            }
        }

        if (localFilepath.value != null) {
            PhotoFileImage(
                localFilepath.value!!,
                modifier = modifier.clickable {
                    fullScreenImageState.value = localFilepath.value
                },
                effect = {
                    VideoLoadingView(modifier)
                },
                contentScale = contentScale
            )
        } else if (imageLoadError.value) {
            Image(
                painter = imageResource(Res.drawable.ic_received_image_not_available),
                contentDescription = "",
                modifier = modifier.aspectRatio(1f)
            )
        } else if (isReplyView) {
            Icon(
                Icons.Default.AttachFile,
                contentDescription = "Attachment",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(29.dp).padding(2.dp)
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
    isReplyView: Boolean = false
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
        if (!isReplyView) {
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
                Spacer(modifier=Modifier.height(8.dp))
                Text(
                    "Pay to unlock this image",
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}