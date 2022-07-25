package chat.sphinx.common.components

import androidx.compose.runtime.Composable
import chat.sphinx.common.state.SphinxState
import chat.sphinx.common.viewmodel.DashboardViewModel


@Composable
expect fun Dashboard(
    sphinxState: SphinxState,
    dashboardViewModel: DashboardViewModel
)