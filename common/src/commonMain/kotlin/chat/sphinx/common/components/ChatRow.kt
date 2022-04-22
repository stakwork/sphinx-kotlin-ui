package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.ChatDetailData
import chat.sphinx.common.state.ChatDetailState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.wrapper.DateTime

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatRow(
    dashboardChat: DashboardChat
) {
    // TODO: Create DashboardChatState...
    val today00: DateTime by lazy {
        DateTime.getToday00()
    }

    Row(
        modifier = Modifier.clickable {
            ChatDetailState.screenState(
                when(dashboardChat) {
                    is DashboardChat.Active.Conversation -> {
                        ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail(
                            dashboardChat.chat.id,
                            dashboardChat.contact.id,
                            dashboardChat
                        )
                    }
                    is DashboardChat.Active.GroupOrTribe -> {
                        ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail(
                            dashboardChat.chat.id,
                            dashboardChat
                        )
                    }
                    else -> ChatDetailData.EmptyChatDetailData
                }
            )

        }.padding(12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .aspectRatio(1f, true)
                .background(SolidColor(Color.Blue), alpha = 0.50f)
        ) {
            Text(
                text = dashboardChat.chatName?.take(1) ?: " "
            )
        }

        Column {

            Row {
                Text(
                    text = dashboardChat.chatName ?: "Unknown Chat",
                    fontSize = 30.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(3f)
                )
                // TODO: Muted icon...
                Text(
                    text = dashboardChat.getDisplayTime(today00),
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Row {
                Text(
                    text = dashboardChat.getMessageText(),
                    fontSize = 15.sp,
                    fontWeight = if (dashboardChat.hasUnseenMessages()) FontWeight.W400 else FontWeight.W700,
                    maxLines = 1
                )
                // TODO: Unread count...
//                dashboardChat.unseenMessageCount?.let { unseenMessageCount ->
//                    Text(
//                        text = unseenMessageCount.toString()
//                    )
//                }

            }
        }


    }

}