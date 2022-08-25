package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.wrapper.message.MessageType
import kotlinx.coroutines.launch
import theme.light_divider
import theme.primary_green
import theme.primary_red

val scope = SphinxContainer.appModule.applicationScope
val dispatchers = SphinxContainer.appModule.dispatchers
@Composable
fun TribeHeaderMessage(chatMessage: ChatMessage) {
    // If any joined tribe will show below text
    chatMessage.groupActionLabelText?.let { groupActionLabelText ->
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(9.dp),
                border = BorderStroke(1.dp, light_divider)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = groupActionLabelText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W300,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }

}

@Composable
fun MemberRequestMessage(chatMessage: ChatMessage, viewModel: ChatViewModel, requestType: MessageType) {

    val subjectName = chatMessage.message.senderAlias?.value ?: ""

    val requestText = when (requestType) {
        is MessageType.GroupAction.MemberRequest -> {
            "$subjectName wants to\n join the tribe"
        }
        is MessageType.GroupAction.MemberApprove -> {
            "  You have approved  \n the request from $subjectName"
        }
        is MessageType.GroupAction.MemberReject -> {
            "  You have declined  \n the request from $subjectName"
        }
        else -> {
            ""
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Card(
            backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(1.dp, light_divider)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = requestText,
                    fontSize = 10.sp,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                )
                IconButton(
                    onClick = {
                        scope.launch(dispatchers.mainImmediate) {
                            viewModel.processMemberRequest(
                                chatMessage.message.sender,
                                chatMessage.message.id,
                                MessageType.GroupAction.MemberApprove
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                        .background(primary_green)
                        .size(24.dp),
                    enabled = requestType != MessageType.GroupAction.MemberReject

                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Accept",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(4.dp)

                    )
                    if (requestType is MessageType.GroupAction.MemberReject) {
                        Surface(
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxSize()
                        )
                        {}
                    }
                }
                IconButton(
                    onClick = {
                        scope.launch(dispatchers.mainImmediate) {
                            viewModel.processMemberRequest(
                                chatMessage.message.sender,
                                chatMessage.message.id,
                                MessageType.GroupAction.MemberReject
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clip(CircleShape)
                        .background(primary_red)
                        .size(24.dp),
                    enabled = requestType != MessageType.GroupAction.MemberApprove
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(4.dp)
                    )
                    if (requestType is MessageType.GroupAction.MemberApprove) {
                        Surface(
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxSize()
                        )
                        {}
                    }
                }
            }
        }
    }
}

@Composable
fun KickOrDeclinedMemberMessage(viewModel: ChatViewModel, requestType: MessageType ) {

    val requestText = if( requestType is MessageType.GroupAction.MemberReject) {
        "The admin\n declined your request"
    } else {
        "The admin has removed you from this tribe"
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Card(
            backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(1.dp, light_divider)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = requestText,
                    fontSize = 10.sp,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                )
                IconButton(
                    onClick = {
                        scope.launch(dispatchers.mainImmediate) {
                            viewModel.deleteTribe()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(primary_red)
                        .width(78.dp)
                        .height(24.dp)
                ) {
                    Text(
                        text = "Delete Tribe",
                        fontSize = 11.sp,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}