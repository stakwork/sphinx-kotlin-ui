package chat.sphinx.common.components.profile

import CommonButton
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.SphinxSplash
import chat.sphinx.common.components.CommonTextField
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.pin.ChangePin
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.components.pin.PINScreenType
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.state.ContentState.windowState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
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
//        undecorated = true,
        icon = sphinxIcon,
    ) {
        AppTheme {
            Box(
//                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {

                Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).verticalScroll(
                    rememberScrollState())) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        PhotoUrlImage(
                            PhotoUrl("https://randomuser.me/api/portraits/men/22.jpg"),
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                "THOMAS",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "1250",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "sat",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 11.sp
                                )
                            }

                        }
                    }
                    tabs()
                }

            }
        }
    }
}

fun onTapClose() {

}

@Composable
fun tabs() {
    var tabIndex by remember { mutableStateOf(0) } // 1.
    val tabTitles = listOf("Basic", "Advanced")
    Column() { // 2.
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.height(30.dp)
                .border(0.5.dp, color = MaterialTheme.colorScheme.onSecondaryContainer),
            backgroundColor =MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.secondary,
            indicator = {
                it.forEach {
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(it), color = Color.Transparent
                    )
                }
            }) { // 3.
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index, // 4.
                    onClick = {
                        tabIndex = index
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }, modifier = Modifier.background(if(tabIndex==index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background )
                ) // 5.
            }
        }
        when (tabIndex) { // 6.
            0 -> Column(modifier = Modifier) {
                Column(modifier = Modifier.padding(12.dp)) {
                    CommonTextField("User Name")
                    Spacer(modifier = Modifier.height(4.dp))
                    CommonTextField("Address")
                    Spacer(modifier = Modifier.height(4.dp))
                    CommonTextField("Route Hint")
                    Row(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Share my profile photos with my contacts",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(true, onCheckedChange = {},colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.secondary, checkedThumbColor = MaterialTheme.colorScheme.secondary),)
                    }                }
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                    CommonTextField("Meeting Server")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Sync more devices",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 12.sp,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    val lockedDashboardViewModel = remember { LockedDashboardViewModel() }
                    val onTapBackUpKeys= remember { mutableStateOf(false) }
                    CommonButton("Backup your keys", textButtonSize = 12.sp) {

                        onTapBackUpKeys.value=true
                    }
                    if(onTapBackUpKeys.value)
                    Window(
                        onCloseRequest = ::onTapClose,
                        title = "Sphinx",
                        state = WindowState(
                            position = WindowPosition.Aligned(Alignment.Center),
                            size = getPreferredWindowSize(400, 500)
                        ),
//        undecorated = true,
//                        icon = sphinxIcon,
                    ){
                        PINScreen(lockedDashboardViewModel, pinScreenType = PINScreenType.ENTER_PIN_TO_BACK_UP_YOUR_KEYS)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    CommonButton(
                        "Save Changes",
                        customColor = MaterialTheme.colorScheme.secondaryContainer,textButtonSize = 12.sp
                    ) {

                    }
                }

//                CommonTextField("User Name")
//                Spacer(modifier = Modifier.height(4.dp))
            }
            1 -> Column() {
                Column() {
                    CommonTextField("Server URL")
                    Spacer(modifier = Modifier.height(4.dp))
//                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)
                    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(
                            "Pin Timeout",
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text("12 Hours", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                    }
                    var sliderState by remember { mutableStateOf(0f) }
                    Slider(
                        value = sliderState,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        steps = 0,
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(activeTrackColor = MaterialTheme.colorScheme.secondary, thumbColor = MaterialTheme.colorScheme.secondary),
                        onValueChange = { newValue ->
                            sliderState = newValue
                        },
                    )
                }
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
                val openChangePinScreen= remember { mutableStateOf(false) }
                Text(
                    "Chane Pin",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        .padding(24.dp).clickable {
                            openChangePinScreen.value=true
                        }, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary
                )
                if(openChangePinScreen.value){
                    ChangePin()
                }
                Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
                Text(
                    "Chane Privacy Pin",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        .padding(24.dp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(150.dp))
                Box(modifier = Modifier.padding(12.dp)){
                    CommonButton(
                        "Save Changes",
                        customColor = MaterialTheme.colorScheme.secondaryContainer, textButtonSize = 12.sp
                    ) {

                    }
                }

            }
        }
    }
}