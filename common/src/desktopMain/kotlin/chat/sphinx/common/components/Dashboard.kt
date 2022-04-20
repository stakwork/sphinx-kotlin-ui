package chat.sphinx.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.*
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.platform.imageResource
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
    val dashboardViewModel = remember { DashboardViewModel() }

    val splitterState = rememberSplitPaneState()
    val hSplitterState = rememberSplitPaneState()

    when (DashboardState.screenState()) {
        DashboardScreenType.Unlocked -> {
            HorizontalSplitPane(
                splitPaneState = splitterState
            ) {
                first(400.dp) {
                    DashboardSidebar(dashboardViewModel)
                }
                second(300.dp) {
                    when(val chatDetailState = ChatDetailState.screenState()) {
                        is ChatDetailData.EmptyChatDetailData -> {
                            // TODO: Splash...
                            SphinxSplash()
                        }
                        is ChatDetailData.SelectedChatDetailData -> {
                            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

                            Scaffold(
                                scaffoldState = scaffoldState,
                                topBar = {
                                    SphinxChatDetailTopAppBar(chatDetailState.dashboardChat)
                                },
                                content = {
                                    // TODO: Show ChatMessageList...
                                    MessageList(chatDetailState)
                                },
                                bottomBar = {
                                    SphinxChatDetailBottomAppBar()
                                }
                            )

                            // TODO: Input Message

                        }
                    }

                }
                splitter {
                    visiblePart {
                        Box(
                            Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colors.background)
                        )
                    }
                    handle {
                        Box(
                            Modifier
                                .markAsHandle()
                                .cursorForHorizontalResize()
                                .background(SolidColor(Color.Gray), alpha = 0.50f)
                                .width(9.dp)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
        DashboardScreenType.Locked -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(SolidColor(Color.Black), alpha = 0.50f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        PINScreen(dashboardViewModel)
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
        title = {
            Text(
                text = chatName
            )
            // TODO: Lighting Indicator...
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 8.dp,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(imageResource(Res.drawable.sphinx_logo), contentDescription = chatName)
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Notifications, contentDescription = "Mute/Unmute")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Phone, contentDescription = "Call")
            }
        }
    )
}

@Composable
fun SphinxChatDetailBottomAppBar() {
    Surface(
        color = Color.Gray,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO: Attachment
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "Attachment")
                }
                // TODO: Giphy Attachment
                // TODO: Emoji Keyboard...
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Face, contentDescription = "Emoji")
                }
            }

            // TODO: Text Input...
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var messageText = ""
                TextField(
                    value = messageText,
                    onValueChange = { newValue -> messageText = newValue },
                    modifier = Modifier.padding(4.dp).fillMaxWidth(),
                    placeholder = { Text("Message") }
                )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // TODO: Price Chip
                    PriceChip()
                    // TODO: Send Actions
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                    // TODO: Record Action
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Mic, contentDescription = "Microphone")
                    }
                }
            }

        }
    }
}
