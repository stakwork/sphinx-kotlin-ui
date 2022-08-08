package signUp

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
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
actual fun ProfileImageView() {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
    val hideCurrentWindow= remember { mutableStateOf(false) }
    if(hideCurrentWindow.value.not())
    Window(
        onCloseRequest = {},
        title = "Sphinx",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(800, 800)
        ),
        undecorated = false,
        icon = sphinxIcon,
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
//                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f).background(MaterialTheme.colorScheme.background)
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Image(
                        painter = imageResource(Res.drawable.ic_desktop_bg_dots),
                        modifier = Modifier.fillMaxWidth().fillMaxHeight().aspectRatio(1f),
                        contentDescription = null, contentScale = ContentScale.Crop
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Text(
                        "You are now on the \n lightening network!",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W600
                    )
                }
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)).align(Alignment.Center), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {

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
                    Row(modifier = Modifier.fillMaxWidth()) {
                        BackButton()
                    }
                    Column (
                        modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                            ){
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tomas", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Box (contentAlignment = Alignment.Center){
                                val strokeColor=MaterialTheme.colorScheme.onBackground
                                Canvas(Modifier.fillMaxWidth().height(1.dp)) {
                                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                                    drawCircle(
                                        color = strokeColor, radius = 120f, style = Stroke(width = 1f, cap = StrokeCap.Round, pathEffect = pathEffect)
                                    )
                                }
                                PhotoUrlImage(
                                    photoUrl = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    firstNameLetter = ("Sachin Kumar").getInitials(),
                                    fontSize = 9
                                )
                            }


                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Drag and drop or", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
                        }
                        Column() {
//                        Spacer(modifier = Modifier.weight(1f))
                            OutlinedButton(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onBackground), colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                                    Text(
                                        "Select Image",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.W400,
                                        fontFamily = Roboto
                                    )
                                }
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                                    Icon(Icons.Outlined.PhotoCamera, contentDescription = "Camera", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.fillMaxWidth().height(35.dp),
                                onClick = {
                                hideCurrentWindow.value=true
                                }
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "Skip",
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
    if(hideCurrentWindow.value)
        ReadyToUse()
}