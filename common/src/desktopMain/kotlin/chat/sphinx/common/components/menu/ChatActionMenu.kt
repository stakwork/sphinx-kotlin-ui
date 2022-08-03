package chat.sphinx.common.components.menu

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import chat.sphinx.common.Res
import chat.sphinx.common.components.ImageFullScreen
import chat.sphinx.common.components.SendReceiveAmountPopup
import chat.sphinx.common.components.chat.FilePreview
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.CustomAlertDialogProvider
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.message.media.isImage
import com.example.compose.badge_red
import com.example.compose.light_divider
import com.example.compose.sphinx_action_menu
import kotlinx.coroutines.launch

@Composable
fun ChatAction(
    chatViewModel: ChatViewModel?,
    modifier: Modifier = Modifier
) {
    chatViewModel?.let {
        chatViewModel?.chatActionsStateFlow?.collectAsState()?.value?.let { chatActionsMode ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .clickable(
                        onClick = {
                            chatViewModel.toggleChatActionsPopup(null)
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
                    .fillMaxSize()
                    .background(color = Color.Black.copy(0.4f))
            ) {
                when (chatActionsMode) {
                    ChatViewModel.ChatActionsMode.MENU -> {
                        ChatActionMenu(chatViewModel)
                    }
                    ChatViewModel.ChatActionsMode.REQUEST -> {
                        SendReceiveAmountPopup(chatViewModel)
                    }
                    ChatViewModel.ChatActionsMode.SEND_AMOUNT -> {
                        SendReceiveAmountPopup(chatViewModel)
                    }
                    ChatViewModel.ChatActionsMode.SEND_TEMPLATE -> {

                    }
                }
            }
        }
    }
}

@Composable
fun ChatActionMenu(
    chatViewModel: ChatViewModel
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .width(300.dp)
            .wrapContentHeight()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(30.dp, 10.dp, 20.dp, 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clickable(
                        onClick = {
                            scope.launch {
                                ContentState.sendFilePickerDialog.awaitResult()?.let { path ->
                                    chatViewModel.toggleChatActionsPopup(null)
                                    chatViewModel.onMessageFileChanged(path)
                                }
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Icon(
                    Icons.Default.AttachFile,
                    "File",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.width(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "File",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Roboto,
                    fontSize = 17.sp
                )
            }
            Divider(color = light_divider, thickness = 0.5.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clickable(
                        onClick = {
//                            chatViewModel.toggleChatActionsPopup(ChatViewModel.ChatActionsMode.REQUEST)
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Image(
                    painter = imageResource(Res.drawable.ic_request),
                    contentDescription = "receive",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.size(18.dp).padding(1.dp)
                )
                Spacer(modifier = Modifier.width(22.dp))
                Text(
                    "Receive",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Roboto,
                    fontSize = 17.sp
                )
            }
            Divider(color = light_divider, thickness = 0.5.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clickable(
                        onClick = {
                            chatViewModel.toggleChatActionsPopup(ChatViewModel.ChatActionsMode.SEND_AMOUNT)
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Image(
                    painter = imageResource(Res.drawable.ic_send),
                    contentDescription = "send",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.size(18.dp).padding(1.dp)
                )
                Spacer(modifier = Modifier.width(22.dp))
                Text(
                    "Send",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Normal,
                    fontFamily = Roboto,
                    fontSize = 17.sp
                )
            }
            Divider(color = light_divider, thickness = 0.5.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clickable(
                        onClick = {
                            chatViewModel.toggleChatActionsPopup(null)
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                Text(
                    "CANCEL",
                    color = badge_red,
                    fontWeight = FontWeight.Normal,
                    fontFamily = SphinxFonts.montserratFamily,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}
