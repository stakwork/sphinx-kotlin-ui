package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.utils.linkify.LinkTag
import chat.sphinx.wrapper.message.retrieveTextToShow
import kotlin.math.roundToInt

@Composable
actual fun NewContactPreview(chatMessage: ChatMessage) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    )
    val color = MaterialTheme.colorScheme.onBackground
    Column {
        val receiverCorner =
            RoundedCornerShape(
                topEnd = 10.dp,
                topStart = 0.dp,
                bottomEnd = 10.dp,
                bottomStart = 0.dp
            )
        val senderCorner =
            RoundedCornerShape(
                topEnd = 0.dp,
                topStart = 10.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            )

        Column {
            Card(
                backgroundColor = if (chatMessage.isReceived) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.inversePrimary,
                shape = if (chatMessage.isReceived) receiverCorner else senderCorner,
                modifier = Modifier.width(350.dp)
            ) {
                Text(chatMessage.message.retrieveTextToShow().toString(), modifier = Modifier.padding(12.dp), fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            }
            Box(modifier = Modifier.size(350.dp, 150.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(4F, 4F)
                    )
                }
                Column(modifier = Modifier.padding(12.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row {
                        Icon(
                            Icons.Default.Contacts,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "New Contact",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600
                            )
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                Text(
                                    "0xdfdf87f7sd6f7s67sdf67sdf687sdf678sdf67s6df87sf68s87df6",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,  modifier = Modifier.fillMaxWidth(0.9f)
                                )
                                Icon(
                                    Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(20.dp).fillMaxWidth(0.1f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.inversePrimary)
                    ) {
                        Text(
                            "ADD CONTACT",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.W600
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
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