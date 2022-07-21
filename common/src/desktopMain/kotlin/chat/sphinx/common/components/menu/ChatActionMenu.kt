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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import com.example.compose.sphinx_action_menu

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun ChatActionMenu(showDialog:Boolean,callBack: (ChatActionMenuEnums) -> Unit) {
    if(showDialog)
    AlertDialog(
        onDismissRequest = {
//            openDialog.value = false
        },
        dialogProvider =UndecoratedWindowAlertDialogProvider ,

        modifier = Modifier.padding(0.dp).height(160.dp).width(250.dp).clip(RoundedCornerShape(5)),
//        contentColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(5),

        backgroundColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().clip(RoundedCornerShape(5)), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
//                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 12.dp, end = 12.dp).clickable {
                    callBack(ChatActionMenuEnums.REQUEST)
                }) {
                    Image(
                        painter = imageResource(Res.drawable.ic_request),
                        contentDescription = "receive",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Receive", color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(12.dp).clickable {
                    callBack(ChatActionMenuEnums.SEND)
                }) {
                    Image(
                        painter = imageResource(Res.drawable.ic_send),
                        contentDescription = "Sphinx Logo",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Send", color = MaterialTheme.colorScheme.tertiary)
                }
//                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {

                    Text("Cancel", color = MaterialTheme.colorScheme.error, modifier = Modifier.clickable {
                        callBack(ChatActionMenuEnums.CANCEL)
                    })

                }

            }
        },
        title = {

        },
        buttons = {
        }
    )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CustomDialogWithResultExample(
    onDismiss: () -> Unit,
) {


}
