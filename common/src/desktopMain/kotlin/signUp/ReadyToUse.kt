package signUp

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.util.getInitials
import views.BackButton

@Composable
actual fun ReadyToUse() {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
    Window(
        onCloseRequest = {},
        title = "Sphinx",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(800, 800)
        ),
        undecorated = true,
        icon = sphinxIcon,
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
//                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f).background(MaterialTheme.colorScheme.background)
            ) {
                Image(
                    painter = imageResource(Res.drawable.ic_desktop_bg_dots),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Card(modifier = Modifier.height(60.dp).width(170.dp), backgroundColor = Color.White, shape = RoundedCornerShape(50),) {
                        Column(modifier = Modifier.size(60.dp).padding(start = 8.dp)) {
                            Spacer(modifier = Modifier.height(2.5.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PhotoUrlImage(
                                    photoUrl = null,
                                    modifier = Modifier
                                        .size(55.dp)
                                        .clip(CircleShape),
                                    firstNameLetter = ("Sachin Kumar").getInitials(),
                                    fontSize = 9
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tomas", color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f).background(MaterialTheme.colorScheme.onSecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("YOU'RE READY", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.W600)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("TO USE SPHINX", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.W600)
                            Spacer(modifier = Modifier.height(48.dp))
                            Text("You can send messages", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Text("Spend", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("123,345,456 sats,", fontSize = 13.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.W600)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("or receive", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("up to", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("823,345,456 sats", fontSize = 13.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.W600)
                            }
                        }
                            Button(
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.fillMaxWidth().height(35.dp),
                                onClick = {

                                }
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "Finish",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.W400,
                                        fontFamily = Roboto
                                    )
                                }
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                }
            }
    }
}