package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.wrapper.PhotoUrl

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun SendSatsPopUp() {
    AlertDialog(
        onDismissRequest = {
//            openDialog.value = false
        },
        dialogProvider = UndecoratedWindowAlertDialogProvider ,

        modifier = Modifier.padding(0.dp).height(500.dp).width(400.dp),
        contentColor = MaterialTheme.colorScheme.surface,

        backgroundColor = MaterialTheme.colorScheme.surface,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                    PhotoUrlImage(
                        PhotoUrl("https://picsum.photos/200/300"),modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape))
                Spacer(modifier = Modifier.height(8.dp).width(250.dp))
                Text("Ricky", color = MaterialTheme.colorScheme.tertiary)

            }
        }, title = {}, confirmButton = {
            OutlinedButton(onClick = {}){
                Text("Send Sats")
            }
        }
    )
}