package chat.sphinx.common.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.store.ChatUIModel

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatList() {
    val chatUIModel = ChatUIModel()
    LazyColumn {
        items(chatUIModel.dashboardChats) { dashboardChat ->
            Text(
                text = dashboardChat.getMessageText()
            )
        }
    }

}