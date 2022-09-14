package chat.sphinx.common.components.tribe

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import chat.sphinx.common.Res
import chat.sphinx.common.components.SendReceiveAmountPopup
import chat.sphinx.common.components.SendTribePaymentPopUp
import chat.sphinx.common.paymentDetail.PaymentDetailTemplate
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.viewmodel.chat.ChatContactViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper_chat.NotificationLevel
import chat.sphinx.wrapper_chat.isMuteChat
import chat.sphinx.wrapper_chat.isOnlyMentions
import chat.sphinx.wrapper_chat.isSeeAll
import kotlinx.coroutines.launch
import theme.badge_red
import theme.light_divider
import theme.primary_blue
import theme.primary_red

@Composable
fun NotificationLevel(
    chatViewModel: ChatViewModel?,
    modifier: Modifier = Modifier
) {
    chatViewModel?.let {
        val state = chatViewModel.notificationLevelStateFlow.collectAsState().value
        state.second?.let {
            if (state.first) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .clickable(
                            onClick = {
                                chatViewModel.hideChatActionsPopup()
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .fillMaxSize()
                        .background(color = Color.Black.copy(0.4f))
                ) {
                    NotificationLevelView(chatViewModel)
                }
            }
        }
    }
}

@Composable
fun NotificationLevelView(
    chatViewModel: ChatViewModel
) {
    chatViewModel.notificationLevelStateFlow.collectAsState().value.second?.let { notificationLevel ->
        Box(
            modifier = Modifier
                .width(300.dp)
                .wrapContentHeight()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp)
                ).clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        "Notification Level",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontFamily = SphinxFonts.montserratFamily,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        Icons.Default.Close,
                        "Close",
                        tint = primary_red,
                        modifier = Modifier.size(40.dp).padding(10.dp).clickable {
                            chatViewModel.closeNotificationLevelPopup()
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(color = if (notificationLevel.isSeeAll()) primary_blue else Color.Transparent)
                        .clickable(
                            onClick = {
                                chatViewModel.setNotificationLevel(NotificationLevel.SeeAll)
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Text(
                        "See All",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(color = if (notificationLevel.isOnlyMentions()) primary_blue else Color.Transparent)
                        .clickable(
                            onClick = {
                                chatViewModel.setNotificationLevel(NotificationLevel.OnlyMentions)
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Text(
                        "Only Mentions",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(color = if (notificationLevel.isMuteChat()) primary_blue else Color.Transparent)
                        .clickable(
                            onClick = {
                                chatViewModel.setNotificationLevel(NotificationLevel.MuteChat)
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                ) {
                    Text(
                        "Mute Chat",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Roboto,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}
