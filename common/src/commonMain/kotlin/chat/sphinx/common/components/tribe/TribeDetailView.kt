package chat.sphinx.common.components.tribe

import androidx.compose.runtime.Composable
import chat.sphinx.common.viewmodel.DashboardViewModel

@Composable
expect fun TribeDetailView(
    dashboardViewModel: DashboardViewModel
)