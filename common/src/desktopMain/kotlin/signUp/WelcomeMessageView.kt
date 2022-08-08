package signUp

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.util.getInitials

@Composable
actual fun WelcomeMessageView() {
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                Spacer(modifier = Modifier.height(250.dp))
                Text("A Message from your friend...", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))
                PhotoUrlImage(
                    photoUrl = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    firstNameLetter = ("Sachin Kumar").getInitials(),
                    fontSize = 9
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Sachin Kumar", color = MaterialTheme.colorScheme.tertiary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Welcome to Sphinx!", color = MaterialTheme.colorScheme.onBackground, fontSize = 11.sp,)
                Column(modifier = Modifier.fillMaxHeight()) {
                    Spacer(modifier = Modifier.weight(1f))


                            Button(
                                shape = RoundedCornerShape(30.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.width(220.dp).height(40.dp),
                                onClick = {
                                    hideCurrentWindow.value=true
                                }
                            ) {
                                 Row (verticalAlignment = Alignment.CenterVertically){
//                                     Spacer(modifier = Modifier.weight(1f))
                                     Box(modifier = Modifier.weight(0.7f), contentAlignment = Alignment.CenterEnd){
                                         androidx.compose.material.Text(
                                             text = "Get Started",
                                             fontSize = 12.sp,
                                             color = MaterialTheme.colorScheme.tertiary,
                                             fontWeight = FontWeight.W400,
                                             fontFamily = Roboto
                                         )
                                     }
                                     Box(modifier = Modifier.weight(0.3f), contentAlignment = Alignment.CenterEnd){
                                         Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.align(Alignment.CenterEnd).size(14.dp))
                                     }
                                 }

                        }


                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
    if(hideCurrentWindow.value)
        PinAndNameView()
}