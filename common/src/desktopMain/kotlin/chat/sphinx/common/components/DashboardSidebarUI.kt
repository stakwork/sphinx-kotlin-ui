package chat.sphinx.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun DashboardSidebarUI(dashboardViewModel: DashboardViewModel) {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val balance = mutableStateOf(0L)

    scope.launch(dispatchers.main) {
        dashboardViewModel.getAccountBalance().collect {
            it?.let { nodeBalance ->
                balance.value = nodeBalance.balance.value
            }
        }
    }
    Box(
        Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column {
            TopAppBar(
                title = { Text(text = "${balance.value} sats", fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary) },
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                elevation = 8.dp,
                navigationIcon = {
                    IconButton(onClick = dashboardViewModel::networkRefresh) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Connection",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        val networkState by dashboardViewModel.networkStateFlow.collectAsState()
                        if (networkState is LoadResponse.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.FlashOn,
                                contentDescription = "Connection Status",
                                tint = if (networkState is Response.Success) {
                                    Color.Green
                                } else {
                                    androidx.compose.material3.MaterialTheme.colorScheme.error
                                } ,
                                modifier = Modifier.size(14.dp)
                            )

                        }
                    }
                }
            )

            TopAppBar(
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                title = {
                    OutlinedTextField(
                        value = text,
                        shape = RoundedCornerShape(64.dp),
                        onValueChange = { newValue -> text = newValue },
//                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
//                        label = { Text("label") },
                        placeholder = { Text("search") }, modifier = Modifier.height(50.dp)
                    )
                },
                elevation = 8.dp,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Person, contentDescription = "Add a person")
                    }
                }
            )

            ChatListUI()
        }
    }
}

@Preview
@Composable
fun DashboardSidebarPreview() {
    MaterialTheme {
        DashboardSidebarUI(DashboardViewModel())
    }
}