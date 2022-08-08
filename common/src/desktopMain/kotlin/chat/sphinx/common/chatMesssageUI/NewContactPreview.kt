package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.linkify.LinkSpec
import chat.sphinx.utils.linkify.LinkTag
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.message.retrieveTextToShow
import kotlin.math.roundToInt

@Composable
actual fun NewContactPreview(
    chatMessage: ChatMessage,
    linkPreview: ChatMessage.LinkPreview.ContactPreview,
    chatViewModel: ChatViewModel
) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    )
    val color = if (chatMessage.isSent) {
        MaterialTheme.colorScheme.inversePrimary
    } else {
        MaterialTheme.colorScheme.onBackground
    }
    Box(
        modifier = Modifier
            .size(350.dp, 168.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .clickable(
                onClick = {
                    chatViewModel.contactLinkClicked(linkPreview.lightningNodeDescriptor)
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
                Image(
                    painter = imageResource(Res.drawable.ic_add_contact),
                    contentDescription = "Add Contact",
                    modifier = Modifier.size(80.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary.copy(0.5f))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "New Contact",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Medium,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(horizontalArrangement = Arrangement.SpaceAround) {
                        Text(
                            linkPreview.lightningNodeDescriptor.value,
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
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = {
                    chatViewModel.contactLinkClicked(linkPreview.lightningNodeDescriptor)
                },
                modifier = Modifier.fillMaxWidth().height(40.dp),
                contentPadding= PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (chatMessage.isSent) {
                        MaterialTheme.colorScheme.inversePrimary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            ) {
                Text(
                    "ADD CONTACT",
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




private data class DottedShape(
    val step: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ) = Outline.Generic(Path().apply {
        val stepPx = with(density) { step.toPx() }
        val stepsCount = (size.width / stepPx).roundToInt()
        val actualStep = size.width / stepsCount
        val dotSize = Size(width = actualStep / 2, height = size.height)
        for (i in 0 until stepsCount) {
            addRect(
                Rect(
                    offset = Offset(x = i * actualStep, y = 0f),
                    size = dotSize
                )
            )
        }
        close()
    })
}