package views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType

@Composable
fun BackButton() {
    IconButton({}){
        Row(verticalAlignment = Alignment.CenterVertically, ){
            IconButton(onClick = {
                LandingScreenState.screenState(LandingScreenType.LandingPage)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Go back", tint = Color.Gray)
            }
            Text("Back", color = MaterialTheme.colorScheme.tertiary )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}