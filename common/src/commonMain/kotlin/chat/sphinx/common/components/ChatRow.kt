
package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.wrapper.DateTime
import androidx.compose.ui.text.font.FontWeight
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.dashboard.ChatListViewModel
import chat.sphinx.concepts.repository.connect_manager.model.NetworkStatus
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.invite.isDelivered
import chat.sphinx.wrapper.invite.isExpired
import chat.sphinx.wrapper.invite.isPaymentPending
import chat.sphinx.wrapper.invite.isReady
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.util.getInitials
import chat.sphinx.wrapper_chat.isMuteChat
import chat.sphinx.wrapper_chat.isOnlyMentions
import theme.primary_green
import theme.primary_red
import theme.selected_chat
import theme.wash_out_received


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatRow(
    dashboardChat: DashboardChat,
    selected: Boolean,
    chatListViewModel: ChatListViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val today00: DateTime by lazy {
        DateTime.getToday00()
    }

    val isInvite: Boolean by lazy {
        (dashboardChat is DashboardChat.Inactive.Invite)
    }

    Row(
        modifier = Modifier
            .clickable {
                if (dashboardChat is DashboardChat.Inactive.Invite) {
                    dashboardChat.invite.let { invite ->
                        dashboardViewModel.toggleQRWindow(true, "INVITE CODE", invite.inviteString.value)
                    }
                    return@clickable
                }

                chatListViewModel.chatRowSelected(dashboardChat)
            }
            .height(62.dp)
            .background(
                if (selected) {
                    selected_chat
                } else {
                    androidx.compose.material3.MaterialTheme.colorScheme.background
                }
            )
    ) {
        Row(modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 12.dp)) {
            Box(modifier = Modifier.size(46.dp)) {
                if (isInvite) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = imageResource(Res.drawable.ic_qr_code),
                        contentDescription = "qr_code",
                    )
                } else {
                    PhotoUrlImage(
                        dashboardChat.photoUrl,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        firstNameLetter = (dashboardChat.chatName ?: "Unknown Chat").getInitials(),
                        color = if (dashboardChat.color != null) Color(dashboardChat.color!!) else null,
                        fontSize = 16
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column {
                val lastMessage = dashboardChat.getMessageText()

                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (lastMessage.isEmpty()) Modifier.fillMaxSize() else Modifier.height(
                        20.dp
                    ),
                ) {
                    Text(
                        text = dashboardChat.chatName ?: "Unknown Chat",
                        fontSize = 15.sp,
                        maxLines = 1,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (dashboardChat is DashboardChat.Active.Conversation) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    if (dashboardChat is DashboardChat.Inactive.Conversation) {
                        Icon(
                            Icons.Filled.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        )
                        Icon(
                            Icons.Default.FlashOn,
                            contentDescription = "Connection Status",
                            tint = Color.Yellow,
                            modifier = Modifier.size(14.dp)
                        )

                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (dashboardChat is DashboardChat.Active) {
                                if (dashboardChat.isMuted())
                                    Icon(
                                        Icons.Filled.NotificationsOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                                    )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = dashboardChat.getDisplayTime(today00),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                                )
                            }
                            (dashboardChat as? DashboardChat.Inactive.Invite)?.let { dashboardChat ->
                                dashboardChat.getInvitePrice()?.let { invitePrice ->
                                    if (dashboardChat.invite.status.isPaymentPending() && invitePrice.value > 0) {
                                        Text(
                                            modifier = Modifier
                                                .background(color = primary_green, shape = RoundedCornerShape(5.dp))
                                                .padding(4.dp, 2.dp),
                                            text = "${invitePrice.asFormattedString(' ')}",
                                            fontSize = 12.sp,
                                            fontFamily = Roboto,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    (dashboardChat as? DashboardChat.Inactive.Invite)?.getInviteIconAndColor()?.let {
                        Icon(
                            it.first,
                            contentDescription = null,
                            modifier = Modifier.height(16.dp).width(20.dp).padding(end = 4.dp),
                            tint = it.second,
                        )
                    }
                    Text(
                        text = dashboardChat.getMessageText(),
                        fontSize = 13.sp,
                        fontFamily = Roboto,
                        fontWeight = if (dashboardChat.hasUnseenMessages()) FontWeight.W400 else FontWeight.W700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    )
                    if (lastMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.height(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            val unseenCountState = dashboardChat.unseenMessageFlow?.collectAsState(0)
                            val unseenMentionsCountState = dashboardChat.unseenMentionsFlow?.collectAsState(0)
                            val isUnseen = (dashboardChat.hasUnseenMessages() || unseenCountState?.value ?: 0 > 0)

                            Text(
                                text = lastMessage,
                                fontSize = 13.sp,
                                fontWeight = if (isUnseen) FontWeight.W700 else FontWeight.W400,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                                color = if (isUnseen)
                                    androidx.compose.material3.MaterialTheme.colorScheme.tertiary else
                                    androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )

                            unseenMentionsCountState?.let {
                                if (it.value != 0L) {
                                    MessageCount("@ ${it.value.toString()}")
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }

                            unseenCountState?.let {
                                if (it.value != 0L) {
                                    val isChatMutedOrOnlyMentions = (dashboardChat.notify?.isMuteChat() == true || dashboardChat.notify?.isOnlyMentions() == true)

                                    MessageCount(
                                        it.value.toString(),
                                        if (isChatMutedOrOnlyMentions) {
                                            wash_out_received
                                        } else {
                                            androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                        },
                                        if (isChatMutedOrOnlyMentions) 0.2f else 1.0f
                                    )
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
fun MessageCount(
    messageCount: String,
    color: Color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
    textAlpha: Float = 1.0f
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = color,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Text(
            text = messageCount,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.defaultMinSize(20.dp).padding(horizontal = 5.dp, vertical = 2.dp).alpha(textAlpha),
            fontFamily = Roboto,
            fontSize = 12.sp
        )
    }
}