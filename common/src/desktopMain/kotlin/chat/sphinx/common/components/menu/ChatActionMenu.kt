package chat.sphinx.common.components.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.CustomAlertDialogProvider
import com.example.compose.sphinx_action_menu

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun ChatActionMenu(
    showDialog: Boolean,
    callBack: (ChatActionMenuEnums) -> Unit
) {
    if (showDialog)
        AlertDialog(
            onDismissRequest = {
//            openDialog.value = false
            },
            dialogProvider = CustomAlertDialogProvider,

            modifier = Modifier.padding(0.dp).height(150.dp).width(250.dp),
//        contentColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(5),

            backgroundColor = MaterialTheme.colorScheme.background,
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(modifier = Modifier.clickable {
                        callBack(ChatActionMenuEnums.REQUEST)
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 12.dp, end = 12.dp)
                        ) {
                            Image(
                                painter = imageResource(Res.drawable.ic_request),
                                contentDescription = "receive",
                                colorFilter = ColorFilter.tint(color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Receive",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.clickable {
                        callBack(ChatActionMenuEnums.SEND)
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        ) {
                            Image(
                                painter = imageResource(Res.drawable.ic_send),
                                contentDescription = "Sphinx Logo",
                                colorFilter = ColorFilter.tint(color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Send",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp
                            )
                        }
//                Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("CANCEL",

                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().clickable { callBack(ChatActionMenuEnums.CANCEL) }, textAlign = TextAlign.Center)

                }
            },
            title = null,

            buttons = {}
        )
}
