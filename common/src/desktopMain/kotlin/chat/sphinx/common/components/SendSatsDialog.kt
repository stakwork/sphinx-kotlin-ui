package chat.sphinx.common.components

import CommonButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import chat.sphinx.wrapper.PhotoUrl


@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun SendSatsDialog(onConfirm: () -> Unit) {
    val text = remember { mutableStateOf("") }
    val sats = remember { mutableStateOf("") }
    val hasConfirmed = remember { mutableStateOf(false) }
    Dialog(
        onCloseRequest = {},
        undecorated = true, transparent = true, resizable = true, focusable = true, enabled = true
    ) {
        Card(
            elevation = 8.dp,
            backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp).fillMaxSize().verticalScroll(rememberScrollState()).width(800.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text("SEND PAYMENT", color = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    PhotoUrlImage(PhotoUrl("https://picsum.photos/200/300"),modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape))
                    Spacer(modifier = Modifier.height(8.dp).width(250.dp))
                    Text("Ricky", color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(40.dp).width(250.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        TextField(
//                       shape = RoundedCornerShape(68.dp),
                            modifier=Modifier.width(100.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer, textColor = MaterialTheme.colorScheme.tertiary
                            ),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 40.sp),
                            value = sats.value,
                            onValueChange = {
                                sats.value = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = {
//                                Text("Enter Sats", color = MaterialTheme.colorScheme.tertiary.copy(
//                                    alpha = 0.2f
//                                ))
//                            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
//                                Text(
//                                    text = stringResource(id = R.string.notes),
//                                    style = typography.body2
//                                )
//                            }
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
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
                            backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.tertiary.copy(
                                alpha = 0.2f
                            ), textColor = MaterialTheme.colorScheme.tertiary
                        ),
                        value = text.value,
                        onValueChange = {
                            text.value = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = {
                            Text("Enter Message", color = MaterialTheme.colorScheme.tertiary.copy(
                                alpha = 0.2f
                            ))
                        }
                    )
                    Spacer(modifier = Modifier.height(50.dp).width(250.dp))
                    Box(modifier = Modifier.width(120.dp)) {
                        CommonButton("Continue") {}
                    }

                }
            }
        }
    }
//        if (!hasConfirmed.value) {
//            Dialog(
//                visible = true,
////                backgroundColor = MaterialTheme.colorScheme.background,
////
////                title = { Row (modifier = Modifier.fillMaxWidth(),){
//////                    Spacer(modifier = Modifier.weight(1f))
////                    IconButton(onClick = {}){
////                        Icon(Icons.Default.Close,contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
////                    }
////                    Text("Send Payment", color = MaterialTheme.colorScheme.tertiary)
////
////                } },
////                text = {
////
////                },
////                onDismissRequest = {
////                },
////                confirmButton = {
////                    Box(modifier = Modifier.fillMaxWidth(0.8f), contentAlignment = Alignment.Center){
////                        CommonButton("Send"){
////                            hasConfirmed.value = true
////                        }
////                    }
//////                    Button(onClick = {
//////                        hasConfirmed.value = true
//////                    },
//////                    ) {
//////                        Text("Confirm")
//////                    }
////                }
//            )
//        } else {
//            Text("Confirmed")
//        }

}