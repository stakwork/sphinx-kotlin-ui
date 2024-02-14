package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.JitsiCallViewModel
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.message.retrieveSphinxCallLink
import theme.primary_green

@Composable
fun JitsiAudioVideoCall(
    chatMessage: ChatMessage,
    dashboardViewModel: DashboardViewModel,
    jitsiCallViewModel: JitsiCallViewModel,
) {
    val uriHandler = LocalUriHandler.current
    val sphinxCallLink = chatMessage.message.retrieveSphinxCallLink()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(0.4f).padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Call,
                contentDescription = "Call",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "JOIN CALL BY...",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                sphinxCallLink?.audioCallLink?.let {
                    if (dashboardViewModel.isWebViewRestartRequired()) {
                        uriHandler.openUri(it)
                    } else {
                        jitsiCallViewModel.toggleJitsiCallWindow(true, it)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary)
        ) {
            Spacer(modifier = Modifier.weight(1.0f))
            Text(
                "AUDIO",
                color = MaterialTheme.colorScheme.tertiary,
                fontFamily = SphinxFonts.montserratFamily,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Icon(
                Icons.Default.Mic,
                contentDescription = "Mic",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(17.dp)
            )
        }
        if (sphinxCallLink?.startAudioOnly == false) {
            Button(
                onClick = {
                    sphinxCallLink?.videoCallLink?.let {
                        uriHandler.openUri(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = primary_green)
            ) {
                Row {
                    Spacer(modifier = Modifier.weight(1.0f))
                    Text(
                        "VIDEO",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontFamily = SphinxFonts.montserratFamily,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    Icon(
                        Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "COPY LINK",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Default.FileCopy,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}