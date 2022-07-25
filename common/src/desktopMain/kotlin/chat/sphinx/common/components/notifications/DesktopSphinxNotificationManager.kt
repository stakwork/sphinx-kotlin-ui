package chat.sphinx.common.components.notifications

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import chat.sphinx.concepts.notification.SphinxNotificationManager

object DesktopSphinxNotificationManager: SphinxNotificationManager {
//    val sphinxTrayState = TrayState
    val notifications = mutableStateMapOf<Long, Pair<String, String>>()

    override fun notify(
        notificationId: Long,
        groupId: String?,
        title: String,
        message: String
    ) {
        notifications[notificationId] = Pair(
            title,
            message
        )

//        sphinxTrayState.sendNotification(
//            Notification(
//                title = title,
//                message = message,
//                // TODO: Set type...
//            )
//        )
    }

    override fun clearNotification(notificationId: Int) {
        notifications.clear()
        // TODO: implement clear notification functionality for desktop
    }
}