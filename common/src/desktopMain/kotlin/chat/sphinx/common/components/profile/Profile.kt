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
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.QRDetail
import chat.sphinx.common.components.pin.ChangePin
import chat.sphinx.common.components.pin.PINScreen
import chat.sphinx.common.components.pin.PINScreenType
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.ProfileViewModel
import chat.sphinx.common.viewmodel.ResetPinViewModel
import chat.sphinx.common.viewmodel.contact.QRCodeViewModel
import chat.sphinx.common.viewmodel.dashboard.PinExportKeysViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.AppTheme
import com.example.compose.badge_red

@Composable
fun Profile(dashboardViewModel: DashboardViewModel) {

    val viewModel = remember { ProfileViewModel(dashboardViewModel) }
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
                                photoUrl = viewModel.profileState.photoUrl,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = viewModel.profileState.alias,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    val balance by dashboardViewModel.balanceStateFlow.collectAsState()
                                    Text(
                                        text = "${balance?.balance?.value ?: 0}",
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
                        Tabs(viewModel, dashboardViewModel)
                    }

                }
            }
        }
    }
}

fun onTapClose() {

}

@Composable
fun Tabs(viewModel: ProfileViewModel, dashboardViewModel: DashboardViewModel) {
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
            0 -> BasicTab(viewModel, dashboardViewModel)
            1 -> AdvanceTab(viewModel, dashboardViewModel)
        }
    }
}

@Composable
fun AdvanceTab(viewModel: ProfileViewModel, dashboardViewModel: DashboardViewModel) {
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

//        Row(
//            horizontalArrangement = Arrangement.SpaceAround,
//            modifier = Modifier.padding(horizontal = 20.dp)
//        ) {
//            Text(
//                "Pin Timeout",
//                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
//                fontSize = 12.sp
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
//                "12 Hours",
//                color = MaterialTheme.colorScheme.tertiary,
//                fontSize = 12.sp
//            )
//        }
//        var sliderState by remember { mutableStateOf(0f) }
//        Slider(
//            value = sliderState,
//            modifier = Modifier.padding(horizontal = 12.dp),
//            steps = 0,
//            valueRange = 0f..100f,
//            colors = SliderDefaults.colors(
//                activeTrackColor = MaterialTheme.colorScheme.secondary,
//                thumbColor = MaterialTheme.colorScheme.secondary
//            ),
//            onValueChange = { newValue ->
//                sliderState = newValue
//            },
//        )
        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
        val openChangePinScreen = remember { mutableStateOf(false) }
        val resetPinViewModel = remember { ResetPinViewModel()}
        Box(modifier = Modifier.clickable { dashboardViewModel.toggleChangePinWindow(true)}) {
            Text(
                "Change Pin",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            if (dashboardViewModel.changePinWindowStateFlow.collectAsState().value) {
                ChangePin(resetPinViewModel, dashboardViewModel)
            }
        }
        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
//        Box(modifier = Modifier.clickable {  }) {
//            Text(
//                "Change Privacy Pin",
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth().padding(24.dp),
//                fontSize = 12.sp,
//                color = MaterialTheme.colorScheme.secondary
//            )
//        }
//        Divider(color = MaterialTheme.colorScheme.onSecondaryContainer, thickness = 4.dp)
    }
    Column(
        modifier = Modifier.padding(top = 300.dp,start = 40.dp, end = 40.dp, bottom = 24.dp)){
        CommonButton(
            "Save Changes",
            customColor = MaterialTheme.colorScheme.secondaryContainer,
        ) {

        }
    }
}

@Composable
fun BasicTab(viewModel: ProfileViewModel, dashboardViewModel: DashboardViewModel) {

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
                    value = viewModel.profileState.alias,
                    onValueChange = {viewModel.onAliasTextChanged(it)},
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
                        value = viewModel.profileState.nodePubKey,
                        enabled = false,
                        onValueChange = {},
                        modifier = Modifier.weight(1f).padding(top = 8.dp),
                        textStyle = TextStyle(fontSize = 18.sp, color = Color.White, fontFamily = Roboto),
                        singleLine = true,
                        cursorBrush = SolidColor(androidx.compose.material3.MaterialTheme.colorScheme.secondary)
                    )
                    IconButton(onClick = {
                        dashboardViewModel.toggleQRWindow(true)
                    }
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
                    value = viewModel.profileState.routeHint,
                    onValueChange = {},
                    enabled = false,
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
                if(viewModel.profileState.privatePhoto != null) {
                    Switch(
                        checked = !viewModel.profileState.privatePhoto!!,
                        onCheckedChange = { viewModel.onPrivatePhotoSwitchChange(!it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.secondary,
                            checkedThumbColor = MaterialTheme.colorScheme.secondary
                        ),
                    )
                }
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
                    value = viewModel.profileState.meetingServerUrl,
                    onValueChange = {viewModel.onDefaultCallServerChange(it) },
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

            val pinExportKeysViewModel = remember { PinExportKeysViewModel() }
            val onTapBackUpKeys = remember { mutableStateOf(false) }
            val backUpKey = pinExportKeysViewModel.backUpKey.collectAsState().value
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            val copiedToClipBoard = pinExportKeysViewModel.copiedToClipBoard

            CommonButton("Backup your key", endIcon = Icons.Default.VpnKey) {

                dashboardViewModel.toggleBackUpWindow(true)
                onTapBackUpKeys.value = true
            }
            if (dashboardViewModel.backUpWindowStateFlow.collectAsState().value)
                Window(
                    onCloseRequest = {dashboardViewModel.toggleBackUpWindow(false)},
                    title = "Sphinx",
                    state = WindowState(
                        position = WindowPosition.Aligned(Alignment.Center),
                        size = getPreferredWindowSize(400, 500)
                    ),
                ) {
                    PINScreen(
                        pinExportKeysViewModel,
                        pinScreenType = PINScreenType.ENTER_PIN_TO_BACK_UP_YOUR_KEYS,
                        copiedToClipBoard = copiedToClipBoard.value
                    )
                    if(backUpKey != null) {
                        clipboardManager.setText(AnnotatedString(backUpKey))
                        copiedToClipBoard.value = true
                        pinExportKeysViewModel.setBackUpKey(null)
                    }
                }
            Spacer(modifier = Modifier.height(24.dp))
            CommonButton(
                "Save Changes",
                customColor = MaterialTheme.colorScheme.secondaryContainer,
                enabled = viewModel.profileState.saveButtonEnabled,
            ) {
                viewModel.updateOwnerDetails()
            }
            Box(
                Modifier.fillMaxWidth().height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.profileState.status is Response.Error) {
                   Text(
                        text = "There was an error, please try again later",
                        fontSize = 12.sp,
                        fontFamily = Roboto,
                        color = badge_red,
                    )
                }
                if (viewModel.profileState.status is LoadResponse.Loading) {
                    CircularProgressIndicator(
                        Modifier.padding(start = 8.dp).size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
    if (dashboardViewModel.qrWindowStateFlow.collectAsState().value){
        QRDetail(dashboardViewModel, QRCodeViewModel(viewModel.profileState.nodePubKey, null))
    }
    if (viewModel.profileState.status is Response.Success){
        dashboardViewModel.toggleProfileWindow(false)
    }
}
