package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.BubbleBackground
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.message.*
import theme.primary_green
import theme.primary_red


@Composable
fun ReceivedPaidMessageButton(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    if (chatMessage.message.isPaidMessage && chatMessage.isReceived) {
        val status = chatMessage.message.retrievePurchaseStatus() ?: PurchaseStatus.Pending

        val backgroundColor = if (status is PurchaseStatus.Denied) {
            primary_red
        } else {
            primary_green
        }

        Card(
            modifier = modifier.clickable {
                chatViewModel.payAttachment(chatMessage.message)
            },
            backgroundColor = backgroundColor,
            shape = when (chatMessage.background) {
                is BubbleBackground.First.Grouped, BubbleBackground.Middle -> {
                    RoundedCornerShape(topEnd = 0.dp, topStart = 0.dp, bottomEnd = 10.dp, bottomStart = 0.dp)
                }
                else -> {
                    RoundedCornerShape(topEnd = 0.dp, topStart = 0.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
                }
            }
        ) {
            val amount = chatMessage.message.messageMedia?.price?.asFormattedString(' ', appendUnit = true) ?: "0 sat"

            val statusString = when (status) {
                PurchaseStatus.Accepted -> "Purchase Succeeded"
                PurchaseStatus.Denied -> "Purchase Denied"
                PurchaseStatus.Pending -> "PAY"
                PurchaseStatus.Processing -> "Processing Payment..."
            }

            val statusIcon = when (status) {
                PurchaseStatus.Accepted -> Icons.Default.Check
                PurchaseStatus.Denied -> Icons.Default.ErrorOutline
                else -> null
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    if (status.isPurchasePending()) {
                        Image(
                            painter = imageResource(Res.drawable.ic_sent),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
                        )
                    } else if (statusIcon != null) {
                        Icon(
                            statusIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.tertiary,
                            strokeWidth = 2.dp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (status.isPurchasePending()) {
                        Text(
                            statusString,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = SphinxFonts.montserratFamily,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            statusString,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = Roboto,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        amount,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.W400
                    )
                    Spacer(modifier = Modifier.width(12.dp).height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SentPaidMessage(
    chatMessage: ChatMessage,
    modifier: Modifier = Modifier
) {
    if (chatMessage.message.isPaidMessage && chatMessage.isSent) {
        val status = chatMessage.message.retrievePurchaseStatus() ?: PurchaseStatus.Pending
        val amount = chatMessage.message.messageMedia?.price?.asFormattedString(' ', appendUnit = true) ?: "0 sat"

        val text = when (status) {
            PurchaseStatus.Accepted -> "Succeeded"
            PurchaseStatus.Denied -> "Denied"
            PurchaseStatus.Pending -> "Pending"
            PurchaseStatus.Processing -> "Processing payment..."
        }

        Box(
            modifier = modifier
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter)
                    .absoluteOffset(x = 0.dp, y = 0.dp)
            ) {
                Box(contentAlignment = Alignment.CenterStart) {
                    Card(
                        backgroundColor = primary_green, shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            amount.uppercase(),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.W600,
                            fontFamily = Roboto
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(contentAlignment = Alignment.CenterEnd) {
                    Card(
                        backgroundColor = primary_green, shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text,
                            fontWeight = FontWeight.W700,
                            fontSize = 10.sp,
                            fontFamily = Roboto,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }
    }
}