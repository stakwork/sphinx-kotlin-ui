package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.concepts.link_preview.model.toPhotoUrl
import chat.sphinx.utils.linkify.LinkSpec
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
actual fun ExistingTribePreview(
    linkPreview: ChatMessage.LinkPreview.TribeLinkPreview,
    chatViewModel: ChatViewModel
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
                .size(350.dp, 112.dp)
                .padding(16.dp)
                .clickable(
                    onClick = {
                        chatViewModel.tribeLinkClicked(linkPreview.joinLink)
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            PhotoUrlImage(
                linkPreview.imageUrl?.toPhotoUrl(),
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    linkPreview.name.value,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                linkPreview.description?.let { description ->
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.SpaceAround) {
                        Text(
                            description.value,
                            color = MaterialTheme.colorScheme.tertiary.copy(0.5f),
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}