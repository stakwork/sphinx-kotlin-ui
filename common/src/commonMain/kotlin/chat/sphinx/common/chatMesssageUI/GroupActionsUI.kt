package chat.sphinx.common.chatMesssageUI

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.ChatMessage
import theme.light_divider
import theme.primary_green
import theme.primary_red

@Composable
fun TribeHeaderMessage(chatMessage:ChatMessage) {
    // If any joined tribe will show below text
    chatMessage.groupActionLabelText?.let { groupActionLabelText ->
        Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
            Card(backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(9.dp),
                border = BorderStroke(1.dp, light_divider)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = groupActionLabelText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W300,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun MemberRequestHeaderMessage(){
    val newMemberName = "Tomas"

    val pendingRequest = "$newMemberName wants to\n join the tribe"
    val approvedRequest = "  You have approved  \n the request from $newMemberName"
    val declinedRequest = "  You have declined  \n the request from $newMemberName"

    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        Card(backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = RoundedCornerShape(9.dp),
            border = BorderStroke(1.dp, light_divider)
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = pendingRequest,
                    fontSize = 10.sp,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.tertiary
                )
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(CircleShape)
                        .background(primary_green)
                        .size(24.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Accept",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(4.dp)


                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clip(CircleShape)
                        .background(primary_red)
                        .size(24.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decline",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(4.dp)
                    )
                }

            }
        }
    }
}