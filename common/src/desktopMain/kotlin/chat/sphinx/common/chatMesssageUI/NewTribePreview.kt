package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.concepts.link_preview.model.toPhotoUrl
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.linkify.LinkSpec
import chat.sphinx.wrapper.PhotoUrl

@Composable
actual fun NewTribePreview(
    linkPreview: ChatMessage.LinkPreview.TribeLinkPreview,
    chatViewModel: ChatViewModel
) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    )
    val color = MaterialTheme.colorScheme.onBackground
    Box(
        modifier = Modifier
            .size(350.dp, 168.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .clickable(
                onClick = {
                    chatViewModel.tribeLinkClicked(linkPreview.joinLink)
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize().padding(top = 4.dp)
        ) {
            val cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = Offset(0f, 0f),
                            size = Size(350.dp.toPx(), 164.dp.toPx()),
                        ),
                        bottomLeft = cornerRadius,
                        bottomRight = cornerRadius,
                    )
                )
            }
            drawPath(
                path,
                color = color,
                style = stroke
            )
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PhotoUrlImage(
                    linkPreview.imageUrl?.toPhotoUrl(),
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
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
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    chatViewModel.tribeLinkClicked(linkPreview.joinLink)
                },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                contentPadding= PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    "SEE TRIBE",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign =  TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontFamily = SphinxFonts.montserratFamily
                )
            }
        }
    }
}