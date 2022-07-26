package chat.sphinx.common.components

import CommonButton
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import chat.sphinx.common.paymentDetail.PaymentDetailTemplate
import chat.sphinx.utils.CustomAlertDialogProvider
import chat.sphinx.wrapper.PhotoUrl


@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun SendReceiveSatsDialog(sendRequest: Boolean, onConfirm: () -> Unit) {
    val text = remember { mutableStateOf("") }
    val sats = remember { mutableStateOf("0") }
    val hasConfirmed = remember { mutableStateOf(false) }
    val showDialog = mutableStateOf(false)

    AlertDialog(
        onDismissRequest = {
//            openDialog.value = false
        },
        dialogProvider =CustomAlertDialogProvider ,

//        modifier = Modifier.customDialogModifier(CustomDialogPosition.BOTTOM),
        contentColor = MaterialTheme.colorScheme.background,

        backgroundColor = MaterialTheme.colorScheme.background,
        text = {
            Column(
                modifier = Modifier
                    .height(350.dp).verticalScroll(rememberScrollState()).width(300.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.clickable {
                                onConfirm()
                            }.size(14.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 0.dp), horizontalArrangement = Arrangement.Center) {
                            Text(if(sendRequest)"SEND PAYMENT" else "REQUEST PAYMENT", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp).width(250.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        TextField(
//                       shape = RoundedCornerShape(68.dp),
                            modifier=Modifier.width(100.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.background,
                                backgroundColor = MaterialTheme.colorScheme.background,
                                unfocusedBorderColor = MaterialTheme.colorScheme.background, textColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            ),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 40.sp),
                            value = sats.value,
                            onValueChange = {
                                sats.value = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = {
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Sats",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp).width(250.dp))
                    TextField(
//                       shape = RoundedCornerShape(68.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                            backgroundColor = MaterialTheme.colorScheme.background,
                            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(
                                alpha = 0.2f
                            ), textColor = MaterialTheme.colorScheme.tertiary
                        ),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        value = text.value,
                        onValueChange = {
                            text.value = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = {
                            Text("Memo", color = MaterialTheme.colorScheme.tertiary.copy(
                                alpha = 0.2f
                            ), textAlign = TextAlign.Center)
                        }
                    )
                    Spacer(modifier = Modifier.height(0.dp).width(250.dp))
                    if(showDialog.value)
                        PaymentDetailTemplate{
                            showDialog.value=false
                        }


                }
            }
        }, title = null, confirmButton = {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Box(modifier = Modifier.width(150.dp)
                    .height(60.dp).padding(bottom = 16.dp)) {
                    CommonButton("CONFIRM", textButtonSize = 12.sp, fontWeight = FontWeight.W600) {
                        showDialog.value=true
                    }
                }
            }
        }
    )
//    CustomDialogWithResultExample(onPositiveClick = {}, onNegativeClick = {}, onDismiss = {})
}
