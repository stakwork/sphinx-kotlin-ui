package chat.sphinx.common.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import chat.sphinx.common.state.SphinxState
import chat.sphinx.common.viewmodel.DashboardViewModel

@Composable
actual fun Dashboard(
    sphinxState: SphinxState,
    dashboardViewModel: DashboardViewModel
) {
    Text(
        text = "Dashboard"
    )
}