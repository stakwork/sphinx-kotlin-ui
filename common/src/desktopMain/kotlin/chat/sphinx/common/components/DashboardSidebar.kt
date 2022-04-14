package chat.sphinx.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import chat.sphinx.common.viewmodel.DashboardViewModel

@Composable
fun DashboardSidebar(dashboardStore: DashboardViewModel) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val balance by remember { mutableStateOf(TextFieldValue("0")) }

    Box(Modifier.background(SolidColor(Color.Gray), alpha = 0.40f).fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text(text = "${balance.text} sats") },
                backgroundColor = Color.Gray,
                elevation = 8.dp,
                navigationIcon = {
                    IconButton(onClick = dashboardStore::networkRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Connection")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Phone, contentDescription = "Connection Status")
                    }
                }
            )

            TopAppBar(
                backgroundColor = Color.Gray,
                title = {
                    TextField(
                        value = text,
                        onValueChange = { newValue -> text = newValue },
//                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
//                        label = { Text("label") },
                        placeholder = { Text("search") }
                    )
                },
                elevation = 8.dp,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Person, contentDescription = "Add a person")
                    }
                }
            )

            ChatList()
        }
    }
}

@Preview
@Composable
fun DashboardSidebarPreview() {
    MaterialTheme {
        DashboardSidebar(DashboardViewModel())
    }
}