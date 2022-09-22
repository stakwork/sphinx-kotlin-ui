package chat.sphinx.common.components.tribe

import CommonButton
import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.ImageLoadingView
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.QRDetail
import chat.sphinx.common.components.chat.KebabMenu
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.state.fullScreenImageState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.chat.TribeDetailViewModel
import chat.sphinx.common.viewmodel.contact.QRCodeViewModel
import chat.sphinx.response.LoadResponse
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import theme.badge_red
import utils.deduceMediaType

@Composable
actual fun TribeDetailView(dashboardViewModel: DashboardViewModel, chatId: ChatId) {

    val viewModel = TribeDetailViewModel(dashboardViewModel, chatId)
    val scope = rememberCoroutineScope()

    Window(
        onCloseRequest = {
            dashboardViewModel.toggleTribeDetailWindow(false,  null)
        },
        title = "Sphinx",

        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(420, 560)
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant).padding(32.dp)
        ) {
            TopHeader(dashboardViewModel, viewModel)

            TribeTextField(
                "Alias",
                viewModel.tribeDetailState.userAlias
            ) {
                viewModel.onAliasTextChanged(it)
            }
            Box(
                modifier = Modifier
            ) {
                TribeTextField(
                    "Profile Picture",
                    viewModel.tribeDetailState.myPhotoUrl?.value ?: "",
                    Modifier.padding(end = 50.dp),
                    false
                ) {}
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .align(Alignment.TopEnd)
                        .wrapContentSize()
                ){
                    val onImageClick = {
                        scope.launch {
                            ContentState.sendFilePickerDialog.awaitResult()?.let { path ->
                                if (path.deduceMediaType().isImage) {
                                    viewModel.onProfilePictureChanged(path)
                                }
                            }
                        }
                    }

                    viewModel.tribeDetailState.myPhotoUrl?.let {
                        PhotoUrlImage(
                            photoUrl = it,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onImageClick.invoke()
                                }
                        )
                    }
                    viewModel.tribeDetailState.userPicture?.let {
                        PhotoFileImage(
                            it.filePath,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onImageClick.invoke()
                                },
                            effect = {}
                        )
                    }
                }
            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text(
//                    "Privacy Setting",
//                    fontSize = 12.sp,
//                    color = Color.Gray,
//                    modifier = Modifier.padding(start = 10.dp)
//                )
//                IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
//                    Icon(
//                        Icons.Outlined.Help,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier.size(12.dp)
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(
//                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text("Select PIN", fontSize = 17.sp, color = MaterialTheme.colorScheme.tertiary)
//                Tabs()
//            }
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if (viewModel.tribeDetailState.updateResponse is LoadResponse.Loading) {
                CircularProgressIndicator(
                    Modifier.padding(20.dp).size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
            CommonButton(
                enabled = viewModel.tribeDetailState.saveButtonEnable,
                text = "SAVE",
                callback = {
                    viewModel.updateUserInfo()
                }
            )
        }
    }
}

@Composable
fun TribeTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onTextChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = Roboto,
            color = MaterialTheme.colorScheme.onBackground,
        )
        BasicTextField(
            value = value,
            onValueChange = {
                onTextChange(it)
            },
            modifier = modifier.fillMaxWidth().padding(top = 4.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = Color.White, fontFamily = Roboto),
            singleLine = true,
            enabled = enabled,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)

        )
        Divider(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun TopHeader(dashboardViewModel: DashboardViewModel, viewModel: TribeDetailViewModel) {
    val showOptionMenu = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
    ) {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            PhotoUrlImage(photoUrl = viewModel.tribeDetailState.tribePhotoUrl, modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = viewModel.tribeDetailState.tribeName,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Column {
                Text(viewModel.tribeDetailState.createDate, color = MaterialTheme.colorScheme.onBackground, fontSize = 11.sp)
                Text(
                    viewModel.tribeDetailState.tribeConfigurations,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 10.sp
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            KebabMenu(
                contentDescription = "Menu",
            ) {
                showOptionMenu.value = true
            }
            CursorDropdownMenu(
                expanded = showOptionMenu.value,

                onDismissRequest = {showOptionMenu.value = false},
                modifier = Modifier.background(MaterialTheme.colorScheme.inversePrimary).clip(
                    RoundedCornerShape(16.dp)
                )
            ) {
                if (viewModel.tribeDetailState.tribeOwner) {
                    Spacer(modifier = Modifier.height(4.dp))
                    DropdownMenuItem(
                        modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                        onClick = {
                            dashboardViewModel.toggleQRWindow(true, "TRIBE JOIN LINK", viewModel.tribeDetailState.shareTribeUrl)
                            showOptionMenu.value = false
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share tribe QR Code", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.onBackground)
                    DropdownMenuItem(
                        modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                        onClick = {
                            viewModel.exitAndDeleteTribe()
                            showOptionMenu.value = false
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "",
                                tint = badge_red, modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Tribe", color = badge_red, fontSize = 12.sp)
                        }
                    }
                } else {
                    DropdownMenuItem(
                        modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                        onClick = {
                            viewModel.exitAndDeleteTribe()
                            showOptionMenu.value = false
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exit Tribe", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun Tabs() {
    var tabIndex by remember { mutableStateOf(0) } // 1.
    val tabTitles = listOf("Standard", "Private")
    Card (border = BorderStroke(0.1.dp, Color.Gray), backgroundColor = Color.Transparent){

        Row {
            Card(
                border = BorderStroke(0.1.dp, Color.Gray),
                backgroundColor = Color.Gray
            ) {
                Text(
                    tabTitles.get(0),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Card(
                shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    tabTitles.get(1),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
            }
        }
    }
}