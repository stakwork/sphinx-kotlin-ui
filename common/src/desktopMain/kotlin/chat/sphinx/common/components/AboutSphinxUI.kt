package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import kotlinx.coroutines.flow.first

@Composable
fun AboutSphinx(dashboardViewModel: DashboardViewModel) {
    var isOpen by remember { mutableStateOf(true) }
    if (isOpen) {
        Window(
            onCloseRequest = {
                dashboardViewModel.toggleAboutSphinxWindow(false)
            },
            title = "About Sphinx",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(300, 190)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(4.dp))

                Image(
                    painter = imageResource(Res.drawable.sphinx_logo),
                    contentDescription = "Sphinx Logo",
                    modifier = Modifier.height(50.dp).width(50.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Sphinx",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Version " + dashboardViewModel.packageVersionAndUpgrade.value.first,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Light,
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Copyright © 2020 Stakwork. All rights reserved.",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Light,
                    fontSize = 11.sp
                )
            }
        }
    }
}