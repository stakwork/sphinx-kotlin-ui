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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.chat.AttachmentPreview
import chat.sphinx.common.components.menu.ChatAction
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.components.tribe.JoinTribeView
import chat.sphinx.common.components.tribe.TribeDetailView
import chat.sphinx.common.components.profile.Profile
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
import chat.sphinx.common.viewmodel.chat.ChatContactViewModel
import chat.sphinx.common.viewmodel.chat.ChatTribeViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.contact.QRCodeViewModel
import chat.sphinx.components.browser.SphinxFeedUrlViewer
import chat.sphinx.components.browser.WebAppBrowserWindow
import chat.sphinx.platform.imageResource
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.utils.onKeyUp
import chat.sphinx.wrapper.chat.isMuted
import chat.sphinx.wrapper.chat.isPending
import chat.sphinx.wrapper.chat.isPrivateTribe
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.util.getInitials
import theme.place_holder_text
import theme.primary_green
import theme.sphinx_orange
import kotlinx.coroutines.launch
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import theme.primary_blue
import utils.AnimatedContainer
import java.awt.Cursor

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.cursorForHorizontalResize(): Modifier =
    pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
actual fun Dashboard(
    sphinxState: SphinxState,
    dashboardViewModel: DashboardViewModel
) {
    val splitterState = rememberSplitPaneState()
    var chatViewModel: ChatViewModel? = null

    when (DashboardScreenState.screenState()) {
        DashboardScreenType.Unlocked -> {

            dashboardViewModel.screenInit()

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

                    chatViewModel?.readMessages()
                    chatViewModel?.cancelMessagesJob()

                    chatViewModel = when (chatDetailState) {
                        is ChatDetailData.SelectedChatDetailData.SelectedContactDetail -> {
                            ChatContactViewModel(null, chatDetailState.contactId!!, dashboardViewModel)
                        }
                        is ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail -> {
                            ChatContactViewModel(chatDetailState.chatId!!, chatDetailState.contactId!!, dashboardViewModel)
                        }
                        is ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail -> {
                            ChatTribeViewModel(chatDetailState.chatId!!, dashboardViewModel)
                        }
                        else -> {
                            null
                        }
                    }

                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            SphinxChatDetailTopAppBar(dashboardChat, chatViewModel, dashboardViewModel) },
                        bottomBar = {
                            SphinxChatDetailBottomAppBar(dashboardChat, chatViewModel)
                        }
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                                .padding(paddingValues),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            chatViewModel?.let { chatViewModel ->
                                MessageListUI(chatViewModel, dashboardViewModel)
                            }
                        }
                        AttachmentPreview(
                            chatViewModel,
                            Modifier.padding(paddingValues)
                        )
                        ChatAction(
                            chatViewModel,
                            Modifier.padding(paddingValues)
                        )
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

            ImageFullScreen(fullScreenImageState)

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
    chatViewModel: ChatViewModel?,
    dashboardViewModel: DashboardViewModel?
) {
    val openWebView= remember { mutableStateOf(false) }
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
    val contactId = chatViewModel?.editMessageState?.contactId

    TopAppBar(
        modifier = Modifier.height(60.dp),
        title = {
            Column {
                Row {
                    Text(
                        text = chatName, fontSize = 16.sp, fontWeight = FontWeight.W700,
                        modifier = Modifier.clickable {
                            if (dashboardChat.isTribe()) {
                                chatViewModel?.chatId?.let { dashboardViewModel?.toggleTribeDetailWindow(true, it) }
                            } else {
                                dashboardViewModel?.toggleContactWindow(true, ContactScreenState.EditContact(contactId))
                            }
                        }
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

            if(SphinxFeedUrlViewer.tribeAppUrl.value?.second?.run { this.toString().isNotEmpty() } == true){
                IconButton(onClick = {
                    openWebView.value=openWebView.value.not()
                }){
                    Icon(Icons.Default.Apps,tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground, contentDescription = "Apps")
                }
            }
            IconButton(onClick = {
                chatViewModel?.toggleChatMuted()
            }) {
                chatViewModel?.let {
                    val chat by chatViewModel.chatSharedFlow.collectAsState(
                        (dashboardChat as? DashboardChat.Active)?.chat
                    )
                    Icon(
                        if (chat?.isMuted() == true) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                        contentDescription = "Mute/Unmute",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            IconButton(onClick = {
                chatViewModel?.sendCallInvite(false)
            }) {
                Icon(
                    Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
    if(openWebView.value){
                    WebAppBrowserWindow(
                getPreferredWindowSize(
                    600,
                    600
                ), onCloseRequest = {
                    openWebView.value=false
                        }
            )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SphinxChatDetailBottomAppBar(
    dashboardChat: DashboardChat?,
    chatViewModel: ChatViewModel?
) {
    val scope = rememberCoroutineScope()

    Surface(
        color = androidx.compose.material3.MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        elevation = 8.dp,
    ) {
        Column {
            MessageReplyingBar(chatViewModel)

            Row(
                modifier = Modifier.fillMaxWidth().defaultMinSize(Dp.Unspecified, 60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = {
                        if (chatViewModel is ChatTribeViewModel) {
                            scope.launch {
                                ContentState.sendFilePickerDialog.awaitResult()?.let { path ->
                                    chatViewModel.hideChatActionsPopup()
                                    chatViewModel.onMessageFileChanged(path)
                                }
                            }
                        } else {
                            chatViewModel?.toggleChatActionsPopup(ChatViewModel.ChatActionsMode.MENU)
                        }
                    },
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
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        CustomTextField(
                            trailingIcon = null,
                            modifier = Modifier
                                .background(
                                    androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .defaultMinSize(Dp.Unspecified, 32.dp)
                                .onKeyEvent(
                                    onKeyUp(
                                        Key.Enter
                                    ) {
                                        chatViewModel?.onSendMessage()
                                    }
                                ),
                            color = Color.White,
                            fontSize = 16.sp,
                            placeholderText = "Message...",
                            singleLine = false,
                            maxLines = 4,
                            onValueChange = {
                                if (chatViewModel != null) run {
                                    chatViewModel.onMessageTextChanged(it.trim())
                                }
                            },
                            value = chatViewModel?.editMessageState?.messageText?.value ?: "",
                            cursorBrush = primary_blue,
                            enabled = !(dashboardChat?.getChatOrNull()?.isPrivateTribe() == true && dashboardChat?.getChatOrNull()?.status?.isPending() == true)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))
                        PriceChip(chatViewModel)
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
