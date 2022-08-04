package chat.sphinx.common.paymentDetail

//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.AlertDialog
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogState
//import chat.sphinx.common.components.PhotoUrlImage
//import chat.sphinx.utils.CustomAlertDialogProvider
//import chat.sphinx.wrapper.PhotoUrl


//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun PaymentDetailTemplate(onDismiss:()->Unit) {
//    AlertDialog(
//        onDismissRequest = {
////            openDialog.value = false
//        },
//        dialogProvider = CustomAlertDialogProvider ,
//
//        modifier = Modifier.height(550.dp).width(400.dp),
//        contentColor = MaterialTheme.colorScheme.background,
//
//        backgroundColor = MaterialTheme.colorScheme.background,
//    text = {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier.background(MaterialTheme.colorScheme.background).height(550.dp).width(400.dp)
//        ) {
//            Text("")
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Icon(
//                    Icons.Default.ArrowBack,
//                    contentDescription = "Close",
//                    tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp).clickable {
//                        onDismiss()
//                    }
//                )
//                Icon(
//                    Icons.Default.Close,
//                    contentDescription = "Close",
//                    tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp).clickable {
//                        onDismiss()
//                    }
//                )
////                IconButton(onClick = {}) {
////
////                }
////                IconButton(onClick = {}) {
////
////                }
//            }
//            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
//                Text("100", color = MaterialTheme.colorScheme.tertiary, fontSize = 40.sp)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Sat", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Tes Message", color = MaterialTheme.colorScheme.tertiary, fontSize = 14.sp)
//            Spacer(modifier = Modifier.height(24.dp))
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                state = rememberLazyListState()
//            ) {
//                item {
//                    Spacer(modifier = Modifier.width(100.dp))
//                }
//                items(10) {
//                    PhotoUrlImage(
//                        photoUrl = PhotoUrl("https://source.unsplash.com/random/200x200?sig=1"),
//                        modifier = Modifier.height(250.dp).width(200.dp).padding(12.dp).clip(RoundedCornerShape(8.dp))
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                state = rememberLazyListState()
//            ) {
//                item {
//                    Spacer(modifier = Modifier.width(100.dp))
//                }
//                items(10) {
//                    PhotoUrlImage(
//                        PhotoUrl("https://picsum.photos/200/300"), modifier = Modifier
//                            .size(60.dp).padding(8.dp)
//                            .clip(CircleShape)
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//                onClick = {},
//                shape = CircleShape,
//                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondaryContainer),
//                modifier = Modifier.fillMaxWidth(0.5f).height(50.dp)
//            ) {
//                Text("CONFIRM", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
//            }
//
//        }
//    }, buttons = {})
//}