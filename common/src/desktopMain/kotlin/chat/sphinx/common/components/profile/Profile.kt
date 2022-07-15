package chat.sphinx.common.components.profile

import CommonButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.CommonTextField
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.state.ContentState.windowState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.utils.onKeyUp
import chat.sphinx.wrapper.PhotoUrl
import com.example.compose.AppTheme
import kotlinx.coroutines.delay

@Composable
actual fun Profile() {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
    Window(
        onCloseRequest = ::onTapClose,
        title = "Sphinx",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(300, 800)
        ),
        undecorated = true,
        icon = sphinxIcon,
    ) {
        AppTheme {
            Box(
//                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {

                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                      Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(24.dp)) {
                          PhotoUrlImage(
                              PhotoUrl("https://randomuser.me/api/portraits/men/22.jpg"),
                              modifier = Modifier
                                  .size(60.dp)
                                  .clip(CircleShape)
                          )
                          Spacer(modifier = Modifier.width(16.dp))
                          Column(verticalArrangement = Arrangement.Center) {
                              Text("THOMAS", color = MaterialTheme.colorScheme.tertiary,    fontWeight = FontWeight.Bold)
                              Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){
                                  Text("1250",color =MaterialTheme.colorScheme.tertiary, fontSize = 11.sp)
                                  Spacer(modifier = Modifier.width(6.dp))
                                  Text("sat",color = MaterialTheme.colorScheme.onBackground, fontSize = 11.sp)
                              }

                          }
                      }
                        tabs()
                    }

            }
        }
    }
}
fun onTapClose(){

}
@Composable
fun tabs() {
    var tabIndex by remember { mutableStateOf(0) } // 1.
    val tabTitles = listOf("Basic", "Advanced", )
    Column { // 2.
        TabRow(
            selectedTabIndex = tabIndex, modifier = Modifier.height(30.dp),) { // 3.
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, // 4.
                    onClick = {
                        tabIndex = index
                              },
                    text = { Text(text = title, fontSize = 12.sp) }) // 5.
            }
        }
        when (tabIndex) { // 6.
            0 -> Column(modifier = Modifier.padding(24.dp)) {
                CommonTextField("User Name")
                Spacer(modifier = Modifier.height(4.dp))
                CommonTextField("Address")
                Spacer(modifier = Modifier.height(4.dp))
                CommonTextField("Route Hint")
                Row (modifier = Modifier.padding(12.dp)){
                    Text("Share my profile photos with my contacts", color = MaterialTheme.colorScheme.tertiary,fontSize = 12.sp, modifier = Modifier.fillMaxWidth(0.6f))
                    Spacer(modifier=Modifier.weight(1f))
                    Switch(true, onCheckedChange = {},)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Spacer(modifier = Modifier.height(4.dp))
                CommonTextField("Meeting Server")
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Text("Sync more devices", color = MaterialTheme.colorScheme.tertiary,fontSize = 12.sp, modifier = Modifier.align(
                    Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(24.dp))
                CommonButton("Back up your keys"){

                }
                Spacer(modifier = Modifier.height(24.dp))
                CommonButton("Save Changes", customColor = MaterialTheme.colorScheme.secondaryContainer){

                }

//                CommonTextField("User Name")
//                Spacer(modifier = Modifier.height(4.dp))
            }
            1 -> Column(modifier = Modifier.padding(24.dp)) {
                CommonTextField("Server URL")
                Spacer(modifier = Modifier.height(4.dp))
//                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Row (horizontalArrangement = Arrangement.SpaceAround){
                    Text("Pin Timeout", color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 12.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("12 Hours", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                }
                var sliderState by remember { mutableStateOf(0f) }
                Slider(
                    value = sliderState,
                    steps = 0,
                    valueRange = 0f..100f,
                    onValueChange = { newValue ->
                        sliderState = newValue
                    },
                )
                                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Text("Chane Pin", modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(24.dp))
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Text("Chane Privacy Pin", modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).padding(24.dp))
                CommonButton("Save Changes", customColor = MaterialTheme.colorScheme.secondaryContainer){

                }

            }
        }
    }
}