package chat.sphinx.common.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.concepts.network.query.chat.model.ChatDto
import chat.sphinx.concepts.network.query.message.model.MessageDto
import chat.sphinx.wrapper.chat.*
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.message.Message
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun DashboardSidebar() {
    var text by remember { mutableStateOf(TextFieldValue("")) }

    Box(Modifier.background(SolidColor(Color.Red), alpha = 0.50f).fillMaxSize()) {
        Column {
            TopAppBar(
                title = { Text(text = "XXXX sats") },
                elevation = 8.dp,
                navigationIcon = {
                    IconButton(onClick = {}) {
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

            ChatList(
                emptyList()
            )
        }
    }
}

@Preview
@Composable
fun DashboardSidebarPreview() {
    MaterialTheme {
        DashboardSidebar()
    }
}