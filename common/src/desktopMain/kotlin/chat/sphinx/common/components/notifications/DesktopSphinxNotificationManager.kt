package chat.sphinx.common.components.notifications

import androidx.compose.runtime.mutableStateListOf
import chat.sphinx.concepts.notification.SphinxNotificationManager

object DesktopSphinxNotificationManager: SphinxNotificationManager {
//    val sphinxTrayState = TrayState()
    val notifications = mutableStateListOf<Pair<String, String>>()

    override fun notify(
        notificationId: Int,
        groupId: String?,
        title: String,
        message: String
    ) {
//        notifications.add(
//            Pair(
//                title,
//                message
//            )
//        )
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