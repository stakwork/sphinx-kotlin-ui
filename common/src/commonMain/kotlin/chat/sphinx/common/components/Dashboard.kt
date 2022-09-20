package chat.sphinx.common.components

import androidx.compose.runtime.Composable
import chat.sphinx.common.viewmodel.DashboardViewModel


@Composable
expect fun Dashboard(
    dashboardViewModel: DashboardViewModel
)