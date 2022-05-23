package chat.sphinx.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import chat.sphinx.concepts.notification.SphinxNotificationManager

object DesktopSphinxNotificationManager: SphinxNotificationManager {
    val sphinxTrayState = TrayState()

    override fun notify(
        notificationId: Int,
        groupId: String?,
        title: String,
        message: String
    ) {
        sphinxTrayState.sendNotification(
            Notification(
                title = title,
                message = message,
                // TODO: Set type...
            )
        )
    }

    override fun clearNotification(notificationId: Int) {
        // TODO: implement clear notification functionality for desktop
    }
}