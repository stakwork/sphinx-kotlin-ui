package chat.sphinx.common.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
@OptIn(ExperimentalComposeUiApi::class)
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
                size = getPreferredWindowSize(210, 40+(78+15) * notifications.size)
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
            ) {

                Column(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(
                            top = 20.dp,
                            end = 20.dp
                        )
                        .fillMaxWidth()
                ) {
                    notifications.forEach { notification ->
                        key(notification.key) {
                            Spacer(Modifier.height(15.dp))
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.background)
                                    .align(Alignment.End)
                                    .clickable {
                                        if (dashboardWindow.isMinimized) {
                                            dashboardWindow.isMinimized = false
                                        }
                                        dashboardWindow.toFront()
                                        notifications.clear()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    IconButton(
                                        onClick = {
                                            notifications.clear()
                                        },
                                        modifier = Modifier.clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondary)
                                            .align(Alignment.TopEnd)
                                            .size(30.dp),
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close notification",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                Column (
                                    modifier = Modifier.padding(
                                        top = 20.dp,
                                        bottom = 10.dp,
                                        start = 10.dp,
                                        end = 10.dp
                                    )
                                ) {
                                    Text(
                                        text = notification.value.first,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.tertiary,
                                    )
                                    Text(
                                        text = notification.value.second,
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.tertiary,
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
        // If we have notifications but the dashboardWindow is active we shouldn't show notifications
        notifications.clear()
    }
}