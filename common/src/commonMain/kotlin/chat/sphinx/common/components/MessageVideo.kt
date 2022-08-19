package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.isPaidMessage
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.viewmodel.chat.retrieveRemoteMediaInputStream
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.wrapper.message.retrieveUrlAndMessageMedia


@Composable
fun MessageVideo(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
){
    val message = chatMessage.message
    val localFilepath = rememberSaveable { mutableStateOf(message.messageMedia?.localFile) }
    val imageLoadError = rememberSaveable { mutableStateOf(false) }
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
                    imageLoadError.value = true
                }
            }
        }
    }


    Box(modifier = Modifier.height(250.dp).width(250.dp).background(Color.Red))
    {

    }
}
