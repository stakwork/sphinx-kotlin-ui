package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.chat.callview.JitsiAudioVideoCall
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.wrapper.lightning.isValidLightningNodePubKey
import chat.sphinx.wrapper.message.*
import androidx.compose.ui.text.font.FontStyle

@Composable
fun ChatMessageUI(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    color: Color
) {
    print("rebuilding ${chatMessage.message.id}")

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
                if (chatMessage.isReceived && chatMessage.groupActionLabelText.isNullOrEmpty() && chatMessage.isDeleted.not()) {
                    ImageProfile(chatMessage, color)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                    if (chatMessage.message.type.isGroupAction()) {
                        // If any joined tribe will show below text
                        TribeHeaderMessage(chatMessage)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DisplayConditionalIcons(
                                chatMessage,
                                chatViewModel,
                                color
                            ) // display icons according to different conditions
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Top,
                        ) {
                            if (chatMessage.isSent) {
                                ChatOptionMenu(chatMessage, chatViewModel)
                            }
                            when {
                                chatMessage.message.isSphinxCallLink -> {
                                    JitsiAudioVideoCall(chatMessage)
                                }
                                chatMessage.message.messageContentDecrypted?.value?.isValidLightningNodePubKey == true -> {
                                    Text("Valid Key")
                                }
                                chatMessage.message.type == MessageType.DirectPayment -> {
                                    DirectPaymentUI(chatMessage, chatViewModel)
                                }
                                chatMessage.isDeleted -> {
                                    Column {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = "This message has been deleted",
                                            fontWeight = FontWeight.W300,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 11.sp,
                                            textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                                        )
                                    }
                                }
                                else -> ChatCard(chatMessage, color, chatViewModel)
                            }
                            if (chatMessage.isReceived && chatMessage.isDeleted.not()) {
                                val isMessageMenuVisible = remember { mutableStateOf(false) }
                                Box(modifier = Modifier.height(50.dp).width(50.dp)) {
                                    ChatOptionMenu(chatMessage, chatViewModel)
                                }
                            }

                        }

                    }

                }
            }
        }
    }
}



