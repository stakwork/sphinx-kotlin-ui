package chat.sphinx.common.components.profile

import CommonButton
import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.components.CommonTextField
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.pin.ChangePin
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.components.pin.PINScreenType
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.LockedDashboardViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl
import com.example.compose.AppTheme

@Composable
fun Profile(dashboardViewModel: DashboardViewModel) {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
    var isOpen by remember { mutableStateOf(true) }
    if (isOpen) {
        Window(
            onCloseRequest = {
                dashboardViewModel.toggleProfileWindow(false)
            },
            title = "Sphinx",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 800)
            ),

            icon = sphinxIcon,
        ) {
            AppTheme {
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {

                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
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
                        Tabs()
                    }

                }
            }
        }
    }
}

fun onTapClose() {

}

@Composable
fun Tabs() {
    var tabIndex by remember { mutableStateOf(0) } // 1.
    val tabTitles = listOf("Basic", "Advanced")
    Column() { // 2.
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.height(30.dp)
                .border(0.5.dp, color = MaterialTheme.colorScheme.onSecondaryContainer),
            backgroundColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.secondary,
            indicator = {
                it.forEach {
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(it), color = Color.Transparent
                    )
                }
            }) { // 3.
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index, // 4.
                    onClick = {
                        tabIndex = index
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    },
                    modifier = Modifier.background(if (tabIndex == index) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.background)
                ) // 5.
            }
        }
        when (tabIndex) { // 6.
            0 -> BasicTab()
            1 -> AdvanceTab()
        }
    }
}

@Composable
fun AdvanceTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(top = 22.dp, start = 32.dp, end = 32.dp)) {
            Column {
                Text(
                    text = "Server URL",
                    fontSize = 12.sp,
                    fontFamily = Roboto,
                    color = Color.Gray,
                )
                BasicTextField(
                    value = "htttp://stakwork.sphinx.chat",
                    onValueChange = {
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White, fontFamily = Roboto),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
                Divider(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                "Pin Timeout",
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "12 Hours",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp
            )
        }
        var sliderState by remember { mutableStateOf(0f) }
        Slider(
            value = sliderState,
            modifier = Modifier.padding(horizontal = 12.dp),
            steps = 0,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                thumbColor = MaterialTheme.colorScheme.secondary
            ),
            onValueChange = { newValue ->
                sliderState = newValue
            },
        )
        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
        val openChangePinScreen = remember { mutableStateOf(false) }
        Box(modifier = Modifier.clickable { openChangePinScreen.value = true }) {
            Text(
                "Change Pin",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            if (openChangePinScreen.value) {
                ChangePin()
            }
        }
        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
        Box(modifier = Modifier.clickable {  }) {
            Text(
                "Change Privacy Pin",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
    }
    Column(
        modifier = Modifier.padding(top = 180.dp,start = 40.dp, end = 40.dp, bottom = 24.dp)){
        CommonButton(
            "Save Changes",
            customColor = MaterialTheme.colorScheme.secondaryContainer,
        ) {

        }
    }
}

@Composable
fun BasicTab() {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(top = 22.dp, start = 32.dp, end = 32.dp)) {
            Column {
                Text(
                    text = "User Name",
                    fontSize = 12.sp,
                    fontFamily = Roboto,
                    color = Color.Gray,
                )
                BasicTextField(
                    value = "Thomas",
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White, fontFamily = Roboto),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
                Divider(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column {
                Text(
                    text = "Address",
                    fontSize = 12.sp,
                    fontFamily = Roboto,
                    color = Color.Gray,
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(28.dp)
                ) {
                    BasicTextField(
                        value = "027dbce35947a3dafc826de411d97990e9b16e78512d1a9e70e87dcc788c2631db",
                        onValueChange = {
                            text = it
                        },
                        modifier = Modifier.weight(1f).padding(top = 8.dp),
                        textStyle = TextStyle(fontSize = 18.sp, color = Color.White, fontFamily = Roboto),
                        singleLine = true,
                        cursorBrush = SolidColor(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                    )
                    IconButton(onClick = {}
                    ) {
                        Icon(
                            Icons.Default.QrCodeScanner,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }
                Divider(modifier = Modifier.padding(top = 4.dp), color = Color.Gray)

            }

            Spacer(modifier = Modifier.height(28.dp))

            Column {
                Text(
                    text = "Route Hint",
                    fontSize = 12.sp,
                    fontFamily = Roboto,
                    color = Color.Gray,
                )
                BasicTextField(
                    value = "027dbce35947a3dafc826de411d97990e9b16e78512d1a9e70e87dcc788c2631db",
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White, fontFamily = Roboto),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
                Divider(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Share my profile photo \nwith contacts",
                    fontSize = 16.sp,
                    fontFamily = Roboto,
                    color = Color.LightGray,
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    true,
                    onCheckedChange = {},
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.secondary,
                        checkedThumbColor = MaterialTheme.colorScheme.secondary
                    ),
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)

        Column(modifier = Modifier.padding(top = 28.dp, start = 32.dp, end = 32.dp)) {
            Column {
                Text(
                    text = "Meeting Server",
                    fontSize = 12.sp,
                    fontFamily = Roboto,
                    color = Color.Gray,
                )
                BasicTextField(
                    value = "https://jitsi.sphinx.chat",
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White, fontFamily = Roboto),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)
                )
                Divider(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 2.dp)

        Column(modifier = Modifier.padding(top = 12.dp, start = 40.dp, end = 40.dp, bottom = 24.dp)) {
            Text(
                "Sync more devices",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 12.sp,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            val lockedDashboardViewModel = remember { LockedDashboardViewModel() }
            val onTapBackUpKeys = remember { mutableStateOf(false) }
            CommonButton("Backup your key", endIcon = Icons.Default.VpnKey) {

                onTapBackUpKeys.value = true
            }
            if (onTapBackUpKeys.value)
                Window(
                    onCloseRequest = ::onTapClose,
                    title = "Sphinx",
                    state = WindowState(
                        position = WindowPosition.Aligned(Alignment.Center),
                        size = getPreferredWindowSize(400, 500)
                    ),
                ) {
                    PINScreen(
                        lockedDashboardViewModel,
                        pinScreenType = PINScreenType.ENTER_PIN_TO_BACK_UP_YOUR_KEYS
                    )
                }
            Spacer(modifier = Modifier.height(24.dp))
            CommonButton(
                "Save Changes",
                customColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {

            }
        }
    }
}
