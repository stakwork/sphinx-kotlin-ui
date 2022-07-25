package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.saveFile
import chat.sphinx.wrapper.message.media.*
import kotlinx.coroutines.launch

@Composable
fun MessageFile(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
) {

    val message = chatMessage.message
    val messageMedia = message.messageMedia
    val localFilepath = messageMedia?.localFile
    val url = messageMedia?.url?.value ?: ""

    LaunchedEffect(url) {
        if (localFilepath == null) {
            chatViewModel.downloadFileMedia(message, chatMessage.isSent)
        }
    }

    if (localFilepath != null) {
        FileUI(messageMedia)
    } else {
        LoadingFile()
    }
}

@Composable
fun LoadingFile() {
    FileUI(null)
}

@Composable
fun FileUI(
    media: MessageMedia?,
) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.height(36.dp).width(220.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            if (media?.mediaType?.isPdf == true) Icons.Default.PictureAsPdf else Icons.Default.FileCopy,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                media?.fileName?.value ?: "Loading...",
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                if (media?.localFile != null)
//                    if (media.mediaType.isPdf)
//                        "1 page"
//                    else
                        media?.localFile?.toFile()?.length()?.toFileSize()?.asFormattedString() ?: "0 KB"
                else
                    "-",
                fontFamily = Roboto,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (media?.localFile != null) {
            Icon(
                Icons.Default.Download,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable {
                    media.localFile?.let { path ->
                        scope.launch {
                            saveFile(
                                media.fileName ?: FileName("File.txt"),
                                path
                            )
                        }
                    }
                }
            )
        } else {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}