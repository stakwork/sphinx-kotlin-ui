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
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(bottom = 120.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box {
                        Card(
                            modifier = Modifier.height(60.dp).width(170.dp),
                            backgroundColor = Color.White,
                            shape = RoundedCornerShape(50),
                        ) {
                            Column(modifier = Modifier.size(60.dp).padding(start = 6.dp, top = 3.dp)) {
                                Spacer(modifier = Modifier.height(2.5.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PhotoUrlImage(
                                        photoUrl = null,
                                        modifier = Modifier
                                            .size(50.dp)
                                        ,
                                        firstNameLetter = ("Sachin Kumar").getInitials(),
                                        fontSize = 9
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tomas", color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
                                }
                            }
                        }
                        Box(
//                            Icons.Filled.ChangeHistory,
//                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.offset(x = 77.dp, y = 55.dp).size(15.dp).rotate(180f).background(shape = TriangleEdgeShape(60), color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary),
//                            contentDescription = null
                        )
                    }
                }
                Box(modifier = Modifier.size(35.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)).align(Alignment.Center), contentAlignment = Alignment.Center) {
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
                    Column(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "YOU'RE READY",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "TO USE SPHINX!",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600
                            )
                            Spacer(modifier = Modifier.height(60.dp))
                            Text(
                                "You can send messages",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "spend",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    "345 456 sats,",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.W600
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    "or receive",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "up to",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "823 456 sats.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.W600
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
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
                        Spacer(modifier = Modifier.height(36.dp))
                    }
                }

            }
        }
    }
}

class TriangleEdgeShape(val offset: Int) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val trianglePath = Path().apply {
            // Moves to top center position
            moveTo(size.width / 2f, 0f)
            // Add line to bottom right corner
            lineTo(size.width, size.height)
            // Add line to bottom left corner
            lineTo(0f, size.width)
        }
        return Outline.Generic(path = trianglePath)
    }
}
