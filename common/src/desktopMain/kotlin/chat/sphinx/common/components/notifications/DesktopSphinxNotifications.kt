package chat.sphinx.common.components.notifications

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.notifications.DesktopSphinxNotificationManager.notifications
import chat.sphinx.utils.getPreferredWindowSize
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.MouseInfo
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener

/**
 * This is a hack to have notifications on all platforms... Could possibly only use it for linux as the mac/windows notifications should work will.
 */
@Composable
fun DesktopSphinxNotifications(
    dashboardWindow: ComposeWindow,
    icon: Painter?
) {
    if (notifications.isNotEmpty() && !dashboardWindow.isActive) {
        Window(
            onCloseRequest = {
                notifications.clear()
            },
            title = "Sphinx Notifications",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.TopEnd),
                size = getPreferredWindowSize(300, 40 + (80 + 15) * notifications.size)
            ),
            alwaysOnTop = true,
            transparent = true,
            resizable = true,
            focusable = false,
            undecorated = true,
            icon = icon
        ) {
            val scope = rememberCoroutineScope()
            var removeNotificationsJob: Job? = null

            val initialLocation = MouseInfo.getPointerInfo().location

            val pointerLocation = mutableStateOf(
                initialLocation
            )

            LaunchedEffect(initialLocation) {
                do {
                    delay(700)
                    pointerLocation.value = MouseInfo.getPointerInfo().location
                } while (initialLocation.equals(pointerLocation.value))

                // Clear notifications...
                if (removeNotificationsJob?.isActive == true) {
                    return@LaunchedEffect
                }
                // Remove notifications when cursor moves...
                removeNotificationsJob = scope.launch {
                    delay(800)
                    notifications.clear()
                }
            }

            val sphinxReadingNotificationsListener = object: MouseMotionListener {
                override fun mouseDragged(e: MouseEvent) {
                }

                override fun mouseMoved(e: MouseEvent) {
                    if (notifications.isNotEmpty()) {
                        if (e.id == MouseEvent.MOUSE_MOVED) {
                            // If the mouse is moved anywhere on the notifications we are reading them
                            removeNotificationsJob?.let {
                                if (it.isActive) {
                                    it.cancel()
                                    return
                                }
                            }
                        }
                    }
                }
            }

            // Notifications will be removed when the mouse moves... unless the mouse is over them
            window.addMouseMotionListener(
                sphinxReadingNotificationsListener
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            )
            {
                Column(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(
                            top = 20.dp,
                            end = 50.dp
                        )
                        .fillMaxWidth()
                        .background(Color.Transparent)
                ) {
                    notifications.forEach { notification ->
                        key(notification.key) {
                            Spacer(Modifier.height(15.dp))
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                                    .align(Alignment.End)
                                    .defaultMinSize(300.dp, 0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close notification",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd)
                                            .clickable {
                                                notifications.remove(notification.key)
                                            }
                                    )
                                }

                                Column (
                                    modifier = Modifier.padding(
                                        top = 15.dp,
                                        bottom = 15.dp,
                                        start = 15.dp,
                                        end = 30.dp
                                    ).clickable {
                                        if (dashboardWindow.isMinimized) {
                                            dashboardWindow.isMinimized = false
                                        }
                                        dashboardWindow.toFront()
                                        notifications.clear()
                                    }
                                ) {
                                    Text(
                                        text = notification.value.first,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = Roboto,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = notification.value.second,
                                        fontWeight = FontWeight.Light,
                                        fontFamily = Roboto,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }


                    }
                }
            }
        }
    }

    if (notifications.isNotEmpty() && dashboardWindow.isActive) {
        LaunchedEffect("NotificationClear") {
            notifications.clear()
        }
    }
}