package chat.sphinx.common.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.notifications.DesktopSphinxNotificationManager.notifications
import chat.sphinx.utils.getFullscreenWindowSize
import chat.sphinx.utils.getPreferredWindowSize
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * This is a hack to have notifications on all platforms... Could possibly only use it for linux as the mac/windows notifications should work will.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DesktopSphinxNotifications(
    icon: Painter?
) {
    if (notifications.isNotEmpty()) {

        Window(
            onCloseRequest = {
                notifications.clear()
            },
            title = "Sphinx Notifications",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.TopEnd),
                size = getFullscreenWindowSize()
            ),
            alwaysOnTop = true,
            transparent = true,
            undecorated = true,
            focusable = false,
            icon = icon
        ) {
            val scope = rememberCoroutineScope()

            var removeNotificationsJob: Job? = null
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onPointerEvent(PointerEventType.Move) {

                        println("Point event: ${this.currentEvent}")
                        if (removeNotificationsJob?.isActive == true) {
                            return@onPointerEvent
                        }
                        // Remove notifications when cursor moves...
                        removeNotificationsJob = scope.launch {
                            delay(1500)
                            notifications.removeFirstOrNull()
                            removeNotificationsJob = null
                        }
//                        if (removeNotificationsJob?.isActive != true) {
//                            removeNotificationsJob = scope.launch {
//                                delay(1500)
//                                notifications.removeFirstOrNull()
//                            }
//                        }
                    }

            ) {
                Column(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    notifications.forEach { notification ->
                        Spacer(Modifier.height(15.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .align(Alignment.End)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                IconButton(
                                    onClick = {
                                        notifications.remove(notification)
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
                                    text = notification.first,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                                Text(
                                    text = notification.second,
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