package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.wrapper.message.*
import androidx.compose.ui.text.font.FontStyle
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.utils.containLinks

@Composable
fun ChatMessageUI(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    dashboardViewModel: DashboardViewModel
) {
    print("rebuilding ${chatMessage.message.id}")

    val bubbleColor = if (chatMessage.isReceived) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.inversePrimary

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth(
                    if (chatMessage.groupActionLabelText.isNullOrEmpty().not()) 1.0f else 0.8f
                ),
            ) {

                /**
                 * Show [ImageProfile] at the starting of chat message if
                 * message is received, message doesn't contains [MessageType.GroupAction] and it's not deleted yet
                 */
                val showProfilePic = (
                        chatMessage.message.type.isGroupAction().not() &&
                        chatMessage.isReceived &&
                        chatMessage.isDeleted.not() &&
                        chatMessage.isFlagged.not()
                        )

                Box(modifier = Modifier.width(42.dp)) {
                    if (showProfilePic) {
                        ImageProfile(
                            chatMessage,
                            Modifier.clickable {
                                if (chatMessage.chat.isTribe()) {
                                    chatViewModel.toggleChatActionsPopup(
                                        ChatViewModel.ChatActionsMode.SEND_TRIBE,
                                        PaymentViewModel.PaymentData(
                                            chatId = chatMessage.chat.id,
                                            messageUUID = chatMessage.message.uuid
                                        )
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp).background(color = Color.Red))
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                    if (chatMessage.message.type.isGroupAction()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val requestType = chatMessage.message.type
                            val senderAlias = chatMessage.message.senderAlias?.value ?: ""

                            if (chatMessage.isAdmin) {
                                if (
                                    requestType == MessageType.GroupAction.MemberRequest ||
                                    requestType == MessageType.GroupAction.MemberApprove ||
                                    requestType == MessageType.GroupAction.MemberReject
                                ) {
                                    MemberRequestMessage(senderAlias, requestType)
                                }
                            }
                            else if (requestType is MessageType.GroupAction.MemberReject) {
                                DeclinedTribeRequestMessage()
                            }
                            else {
                                TribeHeaderMessage(chatMessage)
                            }

                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DisplayConditionalIcons(chatMessage)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Top,
                        ) {
                            when {
                                (chatMessage.isDeleted || chatMessage.isFlagged) -> {
                                    val text = if (chatMessage.isDeleted) {
                                        "This message has been deleted"
                                    } else {
                                        "This message has been flagged"
                                    }

                                    Column {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = text,
                                            fontWeight = FontWeight.W300,
                                            fontFamily = Roboto,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 11.sp,
                                            textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                                        )
                                    }
                                }
                                else -> {
                                    if (chatMessage.isSent) {
                                        ChatOptionMenu(chatMessage, chatViewModel)
                                    }
                                    if (chatMessage.isReceived) {
                                        BubbleArrow(false, bubbleColor)
                                    }

                                    val messageContainsLinks = (chatMessage.message.retrieveTextToShow()?.containLinks() == true)

                                    Column(
                                        modifier = if (messageContainsLinks) {
                                            Modifier.width(350.dp)
                                        } else if (chatMessage.message.isMediaMessage) {
                                            Modifier.fillMaxWidth(0.5f)
                                        } else {
                                            Modifier.weight(1f, fill = false)
                                        }
                                    ) {
                                        ChatCard(
                                            chatMessage,
                                            chatViewModel,
                                            modifier = if (messageContainsLinks) {
                                                Modifier.width(350.dp)
                                            } else {
                                                null
                                            }
                                        )
                                    }
                                    if (chatMessage.isReceived && chatMessage.isDeleted.not()) {
                                        ChatOptionMenu(chatMessage, chatViewModel)
                                    }
                                    if (chatMessage.isSent) {
                                        BubbleArrow(true, bubbleColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BubbleArrow(
    sent: Boolean,
    color: Color,
) {
    val density = LocalDensity.current
    val width = with(density) { 5.dp.roundToPx() }.toFloat()
    val height = with(density) { 7.dp.roundToPx() }.toFloat()

    Canvas(modifier = Modifier.width(5.dp).height(7.dp), onDraw = {
        drawPath(
            color = color,
            path = if (sent) {
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(width, 0f)
                    lineTo(0f, height)
                    lineTo(0f, 0f)
                }
            } else {
                Path().apply {
                    moveTo(0f, 0f)
                    lineTo(width, 0f)
                    lineTo(width, height)
                    lineTo(0f, 0f)
                }
            }
        )
    })
}



