package chat.sphinx.common.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.resources

@Composable
fun ChatList(dashboardChats: List<DashboardChat>) {

    LazyColumn {
        items(dashboardChats) { dashboardChat ->

        }
    }

}