package chat.sphinx.common.components.notifications

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import chat.sphinx.concepts.notification.SphinxNotificationManager
import kotlinx.coroutines.delay

object DesktopSphinxNotificationManager: SphinxNotificationManager {

    val notifications = mutableStateMapOf<Long, Pair<String, String>>()
    var toast: MutableState<SphinxToast?> = mutableStateOf(null)
    var alert: MutableState<SphinxAlertConfirm?> = mutableStateOf(null)

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
    }

    override fun clearNotification(notificationId: Long) {
        notifications.remove(notificationId)
    }

    override suspend fun toast(
        windowTitle: String,
        message: String,
        color: ULong,
        delay: Long
    ) {
        toast.value = SphinxToast(
            windowTitle,
            message,
            Color(color)
        )

        delay(delay)

        toast.value = null
    }

    override suspend fun confirmAlert(
        windowTitle: String,
        title: String,
        message: String,
        confirm: () -> Unit
    ) {
        alert.value = SphinxAlertConfirm(
            windowTitle,
            title,
            message,
            confirm
        )
    }
}

class SphinxAlertConfirm(
    val windowTitle: String,
    val title: String,
    val message: String,
    val confirmCallback: (() -> Unit)? = null
)

class SphinxToast(
    val windowTitle: String,
    val message: String,
    val color: Color
)