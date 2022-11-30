package chat.sphinx.common.components.tribe

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.utils.getPreferredWindowSize

@Composable
fun LeaderboardUI(dashboardViewModel: DashboardViewModel) {
    var isOpen by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val viewModel = remember { dashboardViewModel }

    if (isOpen) {
        Window(
            onCloseRequest = { dashboardViewModel.toggleCreateTribeWindow(false, null) },
            title = "Leaderboard",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 620)
            )
        ) {

        }
    }
}
