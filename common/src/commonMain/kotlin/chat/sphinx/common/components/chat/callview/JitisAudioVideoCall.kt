package chat.sphinx.common.components.chat.callview

import CommonButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import com.example.compose.primary_green

@Composable
fun JitsiAudioVideoCall(chatMessage:ChatMessage){
    val uriHandler = LocalUriHandler.current
    val receiverCorner =
        RoundedCornerShape(topEnd = 10.dp, topStart = 0.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
    val senderCorner =
        RoundedCornerShape(topEnd = 0.dp, topStart = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
    Card(
        backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = if (chatMessage.isReceived) receiverCorner else senderCorner
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(0.4f).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(Icons.Default.Call, contentDescription = "Call",tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Join Call by...",color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
            }
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary)){
                Text("Audio", color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
            }
            Button(onClick = {},modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(backgroundColor = primary_green)){
                Text("Video",color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text("COPY LINK",color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
                IconButton(onClick = {}){
                    Icon(Icons.Default.FileCopy, contentDescription = "", tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(12.dp))
                }
            }

        }
    }

}