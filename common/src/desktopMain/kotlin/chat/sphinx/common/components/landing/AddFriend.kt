package chat.sphinx.common.components.landing

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.utils.getPreferredWindowSize

@Composable
fun AddFriendWindow() {
    var isOpen by remember { mutableStateOf(true) }

    if (isOpen) {
        Window(
            onCloseRequest = {isOpen = false},
            title = "Add Contact",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 580)

            )
        ) {
            AddFriend()
        }
    }
}

@Composable
fun AddFriend(){
    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background )
    ){
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(75.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {},
                modifier = Modifier.clip(CircleShape)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer )
            )
            { Text(
                text = "New to Sphinx",
                fontFamily = Roboto,
                color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
            )
            }
            Divider(Modifier.padding(12.dp), color = Color.Transparent)
            Button(
                onClick = {},
                modifier = Modifier.clip(CircleShape)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary )
            )
            { Text(
                text = "Already on Sphinx",
                fontFamily = Roboto,
                color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
            )
            }
        }
    }
}

