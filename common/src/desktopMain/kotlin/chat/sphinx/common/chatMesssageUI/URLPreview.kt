package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.concepts.link_preview.model.toPhotoUrl
import chat.sphinx.wrapper.toPhotoUrl
import theme.primary_blue

@Composable
actual fun URLPreview(
    linkPreview: ChatMessage.LinkPreview.HttpUrlPreview,
    chatViewModel: ChatViewModel,
    uriHandler: UriHandler
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        ) {}
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
                .clickable(
                    onClick = {
                        uriHandler.openUri(linkPreview.url)
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    linkPreview?.favIconUrl?.let {
                        PhotoUrlImage(
                            linkPreview.favIconUrl?.value?.toPhotoUrl(),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        linkPreview.title?.value ?: "",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 2
                    )
                }
                linkPreview.description?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        description.value,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        maxLines = 3
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    linkPreview.url,
                    color = primary_blue,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            PhotoUrlImage(
                linkPreview.imageUrl?.toPhotoUrl(),
                modifier = Modifier.size(80.dp)
            )
        }
    }
}