package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import views.LoadingShimmerEffect
import views.ShimmerCircleAvatar
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
    val hSplitterState = rememberSplitPaneState()

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
                    when (val chatDetailState = ChatDetailState.screenState()) {
                        is ChatDetailData.EmptyChatDetailData -> {
                            SphinxSplash()
                        }
                        is ChatDetailData.SelectedChatDetailData -> {
                            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

                            Scaffold(scaffoldState = scaffoldState, topBar = {
                                SphinxChatDetailTopAppBar(chatDetailState.dashboardChat)
                            }, bottomBar = {
                                SphinxChatDetailBottomAppBar(chatDetailState.chatViewModel)
                            }) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    MessageListUI()
                                }

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
fun SphinxChatDetailTopAppBar(dashboardChat: DashboardChat) {
    val chatName = dashboardChat.chatName ?: "Unknown Chat"

    TopAppBar(
        modifier = Modifier.height(60.dp),
        title = {
            Column {
                Text(
                    text = chatName, fontSize = 16.sp, fontWeight = FontWeight.W700
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Contributed: 0 sats",
                    fontSize = 14.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                )
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
    chatViewModel: ChatViewModel
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
                    onValueChange = chatViewModel::onMessageTextChanged,
                    value = chatViewModel.editMessageState.messageText
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
                        onClick = chatViewModel::onSendMessage,
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
