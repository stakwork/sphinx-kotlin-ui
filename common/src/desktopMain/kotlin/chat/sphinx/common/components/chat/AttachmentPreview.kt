package chat.sphinx.common.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chat.sphinx.common.components.FileUI
import chat.sphinx.common.components.ImageFullScreen
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.message.media.FileName
import chat.sphinx.wrapper.message.media.MediaType
import chat.sphinx.wrapper.message.media.isImage
import okio.Path


@Composable
fun AttachmentPreview(
    chatViewModel: ChatViewModel?,
    modifier: Modifier = Modifier
) {
    chatViewModel?.editMessageState?.attachmentInfo?.value?.let { attachmentInfo ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.85f))
        ) {
            if (attachmentInfo.mediaType.isImage) {
                ImageFullScreen(attachmentInfo.filePath) {
                    chatViewModel.resetMessageFile()
                }
            } else {
                FilePreview(
                    attachmentInfo.filePath,
                    attachmentInfo.fileName,
                    attachmentInfo.mediaType
                ) {
                    chatViewModel.resetMessageFile()
                }
            }
        }
    }
}

@Composable
fun FilePreview(
    path: Path? = null,
    fileName: FileName? = null,
    mediaType: MediaType? = null,
    callback: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = false, onClick = {} ),
        contentAlignment = Alignment.Center,
    ) {
        FileUI(
            path,
            fileName,
            mediaType
        )
        Box(
            modifier = Modifier.padding(20.dp).align(Alignment.TopStart)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close fullscreen image view",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(30.dp).clickable(enabled = true, onClick = {
                    callback()
                })
            )
        }
    }
}
