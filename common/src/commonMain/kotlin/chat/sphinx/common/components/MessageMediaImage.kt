package chat.sphinx.common.components


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.retrieveRemoteMediaInputStream
import chat.sphinx.concepts.network.client.crypto.CryptoHeader
import chat.sphinx.concepts.network.client.crypto.CryptoScheme
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.meme_server.AuthenticationToken
import chat.sphinx.wrapper.meme_server.headerKey
import chat.sphinx.wrapper.meme_server.headerValue
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.message.media.MessageMedia
import chat.sphinx.wrapper.message.media.token.MediaUrl
import com.example.compose.place_holder_text
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.lazyPainterResource
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import okio.Path

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

    LaunchedEffect(messageMedia.url) {
        if (localFilepath.value == null) {
            messageMedia.url?.value?.let { url ->
                // TODO: Try catch error...
                try {
                    messageMedia.retrieveRemoteMediaInputStream(
                        url,
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
            modifier = modifier
        )
    } else if (imageLoadError.value) {
        Icon(
            Icons.Default.Error,
            "Image Load Error",
            tint = Color.Red,
            modifier = Modifier.size(30.dp).padding(4.dp)
        )
    } else {
        // TODO: Have an error view depending on the launched effect
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = modifier
        )
    }
}