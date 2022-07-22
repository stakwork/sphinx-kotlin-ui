package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.message.retrieveTextToShow

@Composable
actual fun NewTribePreview(chatMessage:ChatMessage) {
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
                Text(chatMessage.message.retrieveTextToShow().toString(), modifier = Modifier.padding(12.dp), fontSize = 12.sp,style = LocalTextStyle.current.copy(textDecoration = TextDecoration.Underline,fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary,))
            }
            Divider( modifier = Modifier.width(350.dp).height(4.dp))
            Box(modifier = Modifier.size(350.dp, 150.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(4F, 4F)
                    )
                }
                Column(modifier = Modifier.padding(12.dp)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhotoUrlImage(
                            PhotoUrl("https://picsum.photos/200/300?random=1"), modifier = Modifier.size(60.dp).clip(
                            RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Tribe Name",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600
                            )
                            Text(
                                "Tribe Description",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,  modifier = Modifier.fillMaxWidth(0.9f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),contentPadding= PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            "SEE TRIBE",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign =  TextAlign.Center,
//                            fontWeight = FontWeight.W600
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}