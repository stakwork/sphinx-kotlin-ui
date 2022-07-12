package chat.sphinx.common.components.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import com.example.compose.sphinx_action_menu

@Composable
actual fun ChatActionMenu(callBack: (ChatActionMenuEnums) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Icon(
        Icons.Default.Add,
        contentDescription = "content description",
        tint = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.size(21.dp).clickable {
            expanded=true
        }
    )

    if(expanded){
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer).clip(
                RoundedCornerShape(16.dp)).width(200.dp).fillMaxWidth()
        ) {
            ActionMenuItem("Library", {
                Image(
                    painter = imageResource(Res.drawable.ic_media_library),
                    contentDescription = "Sphinx Logo",
                    modifier = Modifier.size(15.dp),
                )
            }
            ){}
            ActionMenuItem("GIF",{
                Icon(Icons.Default.Gif,contentDescription = null, tint = sphinx_action_menu,modifier = Modifier.height(15.dp).width(20.dp).scale(1.5f))
            }){}
            ActionMenuItem("File",{
                Icon(Icons.Default.AttachFile,contentDescription = null,tint = sphinx_action_menu, modifier = Modifier.size(15.dp))
            }){}
            ActionMenuItem("Paid Message",{
                Image(
                    painter = imageResource(Res.drawable.ic_paid_message),
                    contentDescription = "Sphinx Logo",
                    modifier = Modifier.size(15.dp)
                )
            }){}
            ActionMenuItem("Request",{
                Image(
                    painter = imageResource(Res.drawable.ic_request),
                    contentDescription = "Sphinx Logo",
                    modifier = Modifier.size(10.dp)
                )
            }){}
            ActionMenuItem("Send",{
                Image(
                    painter = imageResource(Res.drawable.ic_send),
                    contentDescription = "Sphinx Logo",
                    modifier = Modifier.size(10.dp)
                )
            }){
                expanded = false
                callBack(ChatActionMenuEnums.SEND)
            }
        }
    }
}

@Composable
fun ActionMenuItem(text:String,icon:@Composable ()->Unit,callback:()->Unit,) {
    Divider()
    DropdownMenuItem(onClick = { callback() }) {
        Row(verticalAlignment = Alignment.CenterVertically){
            icon()
            Spacer(modifier = Modifier.width(20.dp))
            Text(text, color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
        }
    }
}
