package chat.sphinx.common.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import chat.sphinx.common.Res
import chat.sphinx.common.paymentDetail.PaymentDetailTemplate
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.CustomAlertDialogProvider
import chat.sphinx.wrapper.PhotoUrl


@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun SendSatsPopUp(onClose:()->Unit) {
    AlertDialog(
        onDismissRequest = {
//            openDialog.value = false
        },
        dialogProvider = CustomAlertDialogProvider ,

        modifier = Modifier.width(300.dp).height(350.dp),
        contentColor = MaterialTheme.colorScheme.background,

        backgroundColor = MaterialTheme.colorScheme.background,
        title = null,
    text={
        val sendPaymentDetails= remember { mutableStateOf(false) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxSize()
        ) {


             Text("", color = MaterialTheme.colorScheme.tertiary, fontSize = 1.sp, fontWeight = FontWeight.Bold)
//             Spacer(modifier = Modifier.height(32.dp))
            Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()){
                IconButton(onClick = {
                    onClose()
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)
                    )
                }
            }
//             Text("Ricky", color = MaterialTheme.colorScheme.tertiary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
//             Spacer(modifier = Modifier.height(32.dp))
             PhotoUrlImage(
                 PhotoUrl("https://picsum.photos/200/300"), modifier = Modifier
                     .size(120.dp)
                     .clip(CircleShape)
             )
             Spacer(modifier = Modifier.height(24.dp))
             Text("Ricky", color = MaterialTheme.colorScheme.tertiary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
             Spacer(modifier = Modifier.height(32.dp))
             Button(onClick = {
                 sendPaymentDetails.value=true
             }, shape = CircleShape,colors=ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.surface),
                 border= BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground,), modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
             ){
                 Text("Send Sats", color = MaterialTheme.colorScheme.tertiary)
             }



        }
        if(sendPaymentDetails.value)
            SendReceiveSatsDialog(sendRequest = true) {
                sendPaymentDetails.value=false
            }
    }, dismissButton = {}, confirmButton = {}
    )
}