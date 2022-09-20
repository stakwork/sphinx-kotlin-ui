package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import chat.sphinx.common.viewmodel.DashboardViewModel

@Composable
actual fun Dashboard(
    dashboardViewModel: DashboardViewModel
) {
    Text(
        text = "Dashboard"
    )
}