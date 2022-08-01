package chat.sphinx.common.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.state.fullScreenImageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.retrieveRemoteMediaInputStream
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.message.MessageType
import chat.sphinx.wrapper.message.media.MessageMedia
import chat.sphinx.wrapper.message.retrieveImageUrlAndMessageMedia

@Composable
fun MessageMediaImage(
    message: Message,
    messageMedia: MessageMedia,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier,
) {
    val localFilepath = rememberSaveable { mutableStateOf(messageMedia.localFile) }
    val imageLoadError = rememberSaveable { mutableStateOf(false) }
    val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler
    val url=if(message.type == MessageType.DirectPayment) message.retrieveImageUrlAndMessageMedia()?.second?.templateUrl?.value else messageMedia.url?.value
    LaunchedEffect(url) {
        if (localFilepath.value == null) {
            url?.let { mediaURL ->
                // TODO: Try catch error...
                try {
                    messageMedia.retrieveRemoteMediaInputStream(
                        mediaURL,
                        chatViewModel.memeServerTokenHandler,
                        chatViewModel.memeInputStreamHandler
                    )?.let { imageInputStream ->
                        mediaCacheHandler.createImageFile("jpg").let { imageFilepath -> // TODO: Set extension using filename
                            imageFilepath.toFile().outputStream().use { fileOutputStream ->
                                imageInputStream.copyTo(fileOutputStream)
                                // Update local file...
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
                ImageLoadingView()
            }
        )
    } else if (imageLoadError.value) {
        Icon(
            Icons.Default.Error,
            "Image Load Error",
            tint = Color.Red,
            modifier = Modifier.size(30.dp).padding(4.dp)
        )
    } else {
        ImageLoadingView()
    }
}

@Composable
fun ImageLoadingView() {
    Box(modifier=Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier=Modifier.height(16.dp))
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier=Modifier.height(2.dp))
            Text("Loading/Decrypting...", fontSize = 10.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier=Modifier.height(16.dp))
        }
    }
}