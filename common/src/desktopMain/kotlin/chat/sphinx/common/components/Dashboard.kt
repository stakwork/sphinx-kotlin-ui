package chat.sphinx.common.components

import CommonButton
import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
import chat.sphinx.common.viewmodel.chat.ChatContactViewModel
import chat.sphinx.common.viewmodel.chat.ChatTribeViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.common.components.notifications.DesktopSphinxNotificationManager.notifications
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.util.getInitials
import com.example.compose.place_holder_text
import com.example.compose.primary_green
import com.example.compose.primary_red
import com.example.compose.sphinx_orange
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import utils.AnimatedContainer
import java.awt.Cursor

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
actual fun Dashboard(
    sphinxState: SphinxState
) {
    val splitterState = rememberSplitPaneState()
    var chatViewModel: ChatViewModel? = null

    when (DashboardState.screenState()) {
        DashboardScreenType.Unlocked -> {
            val dashboardViewModel = remember { DashboardViewModel() }

            HorizontalSplitPane(
                splitPaneState = splitterState
            ) {
                first(600.dp) {
                    DashboardSidebarUI(dashboardViewModel)
                }
                second(700.dp) {
                    val chatDetailState = ChatDetailState.screenState()
                    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                    val dashboardChat = (chatDetailState as? ChatDetailData.SelectedChatDetailData)?.dashboardChat

                    chatViewModel?.cancelMessagesJob()
                    chatViewModel = when (chatDetailState) {
                        is ChatDetailData.SelectedChatDetailData.SelectedContactDetail -> {
                            ChatContactViewModel(null, chatDetailState.contactId!!)
                        }
                        is ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail -> {
                            ChatContactViewModel(chatDetailState.chatId!!, chatDetailState.contactId!!)
                        }
                        is ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail -> {
                            ChatTribeViewModel(chatDetailState.chatId!!)
                        }
                        else -> {
                            null
                        }
                    }

                    Scaffold(scaffoldState = scaffoldState, topBar = {
                        SphinxChatDetailTopAppBar(dashboardChat, chatViewModel)
                    }, bottomBar = {
                        SphinxChatDetailBottomAppBar(chatViewModel)
                    }) {
                        Column(
                            modifier = Modifier.fillMaxSize().background(androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            chatViewModel?.let {
                                MessageListUI(it)
                            }
                        }
                    }
                }
                splitter {
                    visiblePart {
                        Box(
                            Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colors.background)
                        )
                    }
                    handle {
                        Box(
                            Modifier.markAsHandle().cursorForHorizontalResize()
                                .background(SolidColor(Color.Gray), alpha = 0.50f).width(9.dp).fillMaxHeight()
                        )
                    }
                }
            }

            fullScreenImageState.value?.let { imagePath ->
                val backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                val fullscreenBackgroundColor = Color(
                    red = backgroundColor.red,
                    green = backgroundColor.green,
                    blue = backgroundColor.blue,
                    alpha = 0.45f
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(fullscreenBackgroundColor)
                        .clickable(enabled = false, onClick = {  })
                ) {
                    PhotoFileImage(
                        imagePath,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // TODO: Figure out which fill works best depending on image size
                    )
                    // Close Fullscreen button
                    Box(
                        modifier = Modifier.padding(40.dp).align(Alignment.TopEnd)
                    ) {
                        IconButton(
                            onClick = {
                                fullScreenImageState.value = null
                            },
                            modifier = Modifier.clip(CircleShape)
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                                .size(40.dp),
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close fullscreen image view",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                }
            }

            val restoreState by dashboardViewModel.restoreStateFlow.collectAsState()
            restoreState?.let { restoreState ->
                if (restoreState.restoring) {
                    RestoreProgressUI(
                        dashboardViewModel,
                        restoreState
                    )
                }
            }
        }
        DashboardScreenType.Locked -> {
            val lockedDashboardViewModel = remember { LockedDashboardViewModel() }
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f).fillMaxHeight().background(SolidColor(Color.Black), alpha = 0.50f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        PINScreen(lockedDashboardViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SphinxChatDetailTopAppBar(
    dashboardChat: DashboardChat?,
    chatViewModel: ChatViewModel?
) {
    if (dashboardChat == null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
        ) {
            Text(
                modifier = Modifier.padding(16.dp, 0.dp),
                text = "Open a conversation to start using Sphinx",
                fontFamily = Roboto,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
            )
        }
        return
    }

    val chatName = dashboardChat?.chatName ?: "Unknown Chat"

    TopAppBar(
        modifier = Modifier.height(60.dp),
        title = {
            Column {
                Row {
                    Text(
                        text = chatName, fontSize = 16.sp, fontWeight = FontWeight.W700
                    )

                    Icon(
                        if (dashboardChat?.isEncrypted()) Icons.Default.Lock else Icons.Default.LockOpen,
                        "Lock",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(23.dp).padding(4.dp, 0.dp, 4.dp, 2.dp)
                    )

                    chatViewModel?.let {
                        val checkRouteResponse by chatViewModel.checkRoute.collectAsState(
                            LoadResponse.Loading
                        )
                        val color = when (checkRouteResponse) {
                            is LoadResponse.Loading -> {
                                androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            }
                            is Response.Error -> {
                                sphinx_orange
                            }
                            is Response.Success -> {
                                primary_green
                            }
                        }

                        Icon(
                            Icons.Default.FlashOn,
                            "Route",
                            tint = color,
                            modifier = Modifier.width(15.dp).height(23.dp).padding(0.dp, 0.dp, 0.dp, 2.dp)
                        )
                    }
                }

                chatViewModel?.let {
                    val chat by chatViewModel.chatSharedFlow.collectAsState(
                        (dashboardChat as? DashboardChat.Active)?.chat
                    )

                    chat?.let { nnChat ->
                        if (nnChat.isTribe()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Price per message: ${nnChat.pricePerMessage?.asFormattedString(' ', false) ?: 0} - Amount to stake: ${nnChat.escrowAmount?.asFormattedString(' ', false) ?: 0}",
                                fontSize = 11.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
            // TODO: Lighting Indicator...
        },
        backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        contentColor = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
        elevation = 8.dp,
        navigationIcon = {
            Spacer(modifier = Modifier.width(14.dp))
            PhotoUrlImage(
                dashboardChat.photoUrl,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                firstNameLetter = (dashboardChat.chatName ?: "Unknown Chat").getInitials(),
                color = if (dashboardChat.color != null) Color(dashboardChat.color!!) else null,
                fontSize = 16
            )
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Mute/Unmute",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            }
        })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SphinxChatDetailBottomAppBar(
    chatViewModel: ChatViewModel?
) {
    Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        elevation = 8.dp
    ) {
        Column {
            MessageReplyingBar(chatViewModel)

            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { },
                    modifier = Modifier.clip(CircleShape)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                        .size(30.dp),
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "content description",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = {}, modifier = Modifier.height(25.dp).width(18.dp)) {
                    Image(
                        painter = imageResource(Res.drawable.ic_giphy),
                        contentDescription = "giphy",
                        contentScale = ContentScale.FillBounds
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.clip(CircleShape)
                        .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
                        .wrapContentSize(),
                ) {
                    Icon(
                        Icons.Outlined.EmojiEmotions,
                        contentDescription = "Emoji",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(30.dp),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomTextField(
                        trailingIcon = null,
                        modifier = Modifier.background(
                            androidx.compose.material3.MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(percent = 50)
                        ).padding(horizontal = 6.dp, vertical = 4.dp).height(32.dp),
                        color = Color.White,
                        fontSize = 16.sp,
                        placeholderText = "Message...",
                        singleLine = false,
                        maxLines = 4,
                        onValueChange = {
                            if (chatViewModel != null) run {
                                if (it.isNotEmpty() && it.last() == '\n') {
                                    chatViewModel.onSendMessage()
                                } else {
                                    chatViewModel.onMessageTextChanged(it)
                                }
                            }
                        },
                        value = chatViewModel?.editMessageState?.messageText?.value ?: ""
                    )
                }
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))
                        PriceChip()
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = {
                                if (chatViewModel != null) run {
                                    chatViewModel.onSendMessage()
                                }
                            },
                            modifier = Modifier.clip(CircleShape)
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                                .size(30.dp),
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send Message",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        // TODO: Record Action
                        IconButton(
                            onClick = {},
                            modifier = Modifier.clip(CircleShape)
                                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
                                .wrapContentSize(),
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Microphone",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun MessageReplyingBar(
    chatViewModel: ChatViewModel?
) {
    chatViewModel?.editMessageState?.replyToMessage?.value?.let { replyToMessage ->
        AnimatedContainer(
            fromTopToBottom = 20,
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth()
                .background(color = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer)
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .height(44.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(
                                if (replyToMessage.replyToMessageColor != null) {
                                    Color(replyToMessage.replyToMessageColor!!)
                                } else {
                                    Color.Gray
                                }
                            )
                    )
                    replyToMessage.message.messageMedia?.let { media ->
                        if (media.mediaType.isImage) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = "Image",
                                tint = Color.Gray,
                                modifier = Modifier.height(88.dp).padding(start = 10.dp)
                            )
                        } else {
                            // show
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Attachment",
                                tint = Color.Gray,
                                modifier = Modifier.height(88.dp).padding(start = 10.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(start = 10.dp, end = 40.dp)
                    ) {
                        Text(
                            replyToMessage.replyToMessageSenderAliasPreview,
                            overflow = TextOverflow.Ellipsis,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.W600,
                            fontSize = 13.sp,
                            maxLines = 1,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            replyToMessage.replyToMessageTextPreview,
                            overflow = TextOverflow.Ellipsis,
                            color = place_holder_text,
                            fontWeight = FontWeight.W400,
                            fontFamily = Roboto,
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                    }
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Close,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                            contentDescription = "Close reply to message",
                            modifier = Modifier.size(20.dp)
                                .align(Alignment.CenterEnd)
                                .clickable(
                                    onClick = {
                                        chatViewModel?.editMessageState?.replyToMessage?.value = null
                                    }
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestoreProgressUI(
    dashboardViewModel: DashboardViewModel,
    restoreState: RestoreProgress
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(SolidColor(androidx.compose.material3.MaterialTheme.colorScheme.background), alpha = 0.5f)
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(SolidColor(androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant), RoundedCornerShape(10.dp))
                .width(300.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Restoring: ${restoreState.progress}%",
                fontFamily = Roboto,
                fontSize = 15.sp,
                fontWeight = FontWeight.W500,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                progress = restoreState.progress.toFloat() / 100,
                modifier = Modifier.fillMaxWidth(0.8f),
                color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(0.8f)) {
                CommonButton(text = "Continue Later") {
                    dashboardViewModel.cancelRestore()
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
