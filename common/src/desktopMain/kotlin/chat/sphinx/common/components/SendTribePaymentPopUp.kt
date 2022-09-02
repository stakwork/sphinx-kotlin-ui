package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import theme.light_divider
import theme.primary_red


@Composable
fun SendTribePaymentPopUp(
    chatViewModel: ChatViewModel,
    paymentViewModel: PaymentViewModel
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(350.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                onClick = {},
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    chatViewModel.hideChatActionsPopup()
                },
            contentAlignment = Alignment.Center,
        ){
            Icon(
                Icons.Default.Close,
                contentDescription = "close",
                tint = primary_red,
                modifier = Modifier.size(18.dp)
            )
        }

        val message by paymentViewModel.messageSharedFlow.collectAsState(null)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(34.dp))
            PhotoUrlImage(
                message?.senderPic,
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                message?.senderAlias?.value ?: "",
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = Roboto,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(45.dp))
            Button(
                modifier = Modifier.width(220.dp).height(50.dp),
                onClick = {
                    chatViewModel.toggleChatActionsPopup(
                        ChatViewModel.ChatActionsMode.SEND_AMOUNT,
                        PaymentViewModel.PaymentData(
                            chatId = paymentViewModel.getPaymentData()?.chatId,
                            messageUUID = paymentViewModel.getPaymentData()?.messageUUID
                        )
                    )
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(1.dp, light_divider),
            ) {
                Text(
                    "Send Sats",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = Roboto,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}