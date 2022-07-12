package chat.sphinx.common.components

import CommonButton
import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.menu.ChatActionMenu
import chat.sphinx.common.components.menu.ChatActionMenuEnums
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
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.asFormattedString
import com.example.compose.primary_green
import com.example.compose.sphinx_orange
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
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
                            modifier = Modifier.fillMaxSize().background(color = androidx.compose.material3.MaterialTheme.colorScheme.background),
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
                    .clip(CircleShape)
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
        modifier = Modifier.fillMaxWidth().height(60.dp),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                var currentSelectedItem by remember { mutableStateOf<ChatActionMenuEnums?>(null) }
                ChatActionMenu{
                  currentSelectedItem=it
                }
                when(currentSelectedItem){
                    ChatActionMenuEnums.LIBRARY -> TODO()
                    ChatActionMenuEnums.GIF -> TODO()
                    ChatActionMenuEnums.FILE -> TODO()
                    ChatActionMenuEnums.PAID_MESSAGE -> TODO()
                    ChatActionMenuEnums.REQUEST -> TODO()
                    ChatActionMenuEnums.SEND -> SendSatsDialog {  }
                    else ->{}
                }
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
                    onValueChange = {
                        if (chatViewModel != null) run {
                            chatViewModel.onMessageTextChanged(it)
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
