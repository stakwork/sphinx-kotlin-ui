package chat.sphinx.common.components.tribe

import CommonButton
import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.notifications.DesktopSphinxToast
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.chat.CreateTribeViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.response.LoadResponse
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.feed.FeedType
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import theme.tribe_hyperlink
import utils.deduceMediaType
import java.awt.Desktop
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

@Composable
fun CreateTribeView(dashboardViewModel: DashboardViewModel, chatId: ChatId?) {
    var isOpen by remember { mutableStateOf(true) }
    var tagPopupState by remember { mutableStateOf(false) }
    var showOptionMenu = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val viewModel = remember { CreateTribeViewModel(dashboardViewModel, chatId) }


    if (isOpen) {
        Window(
            onCloseRequest = { dashboardViewModel.toggleCreateTribeWindow(false, null) },
            title = if (chatId != null) "Edit Tribe" else "New Tribe",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 620)
            )
        ) {
            DesktopSphinxToast("Sphinx")

            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        .padding(38.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    TribeTextField("Name*", viewModel.createTribeState.name) {
                        viewModel.onNameChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                    ) {
                        val path = viewModel.createTribeState.path
                        TribeTextField(
                            label = "Image",
                            value = if (path != null) "" else viewModel.createTribeState.imgUrl,
                            modifier = Modifier.padding(end = 50.dp),
                            enabled = false
                        ) {}
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .wrapContentSize()
                        ) {
                            val onImageClick = {
                                scope.launch {
                                    ContentState.sendFilePickerDialog.awaitResult()?.let { path ->
                                        if (path.deduceMediaType().isImage) {
                                            viewModel.onPictureChanged(path)
                                        }
                                    }
                                }
                            }
                            if (path != null) {
                                PhotoFileImage(
                                    photoFilepath = path,
                                    modifier = Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            onImageClick.invoke()
                                        }
                                )
                            } else {
                                PhotoUrlImage(
                                    photoUrl = viewModel.createTribeState.img,
                                    modifier = if (viewModel.createTribeState.img != null) {
                                        Modifier.size(40.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                onImageClick.invoke()
                                            }
                                    } else {
                                        Modifier.size(40.dp)
                                            .clickable {
                                                onImageClick.invoke()
                                            }
                                    },
                                    placeHolderRes = Res.drawable.ic_tribe_place_holder
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TribeTextField("Description*", viewModel.createTribeState.description) {
                        viewModel.onDescriptionChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TribeTextField(
                        label = "Tags",
                        value = viewModel.createTribeState.tags.toString().replace("[", "").replace("]", ""),
                        modifier = Modifier.clickable(
                            onClick = { tagPopupState = true },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                        enabled = false
                    ) {
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val priceToJoin = viewModel.createTribeState.priceToJoin?.let {
                        it.toString()
                    }
                    TribeTextField("Price to Join", priceToJoin ?: "") {
                        viewModel.onPriceToJoinChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val pricePerMessage = viewModel.createTribeState.pricePerMessage?.let {
                        it.toString()
                    }
                    TribeTextField("Price Per Message", pricePerMessage ?: "") {
                        viewModel.onPricePerMessageChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val amountToStake = viewModel.createTribeState.escrowAmount?.let {
                        it.toString()
                    }
                    TribeTextField("Amount to Stake", amountToStake ?: "") {
                        viewModel.onAmountToStakeChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val timeToStake = viewModel.createTribeState.escrowHours?.let {
                        it.toString()
                    }
                    TribeTextField("Time to Stake (hours)", timeToStake ?: "") {
                        viewModel.onTimeToStakeChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TribeTextField("App Url", viewModel.createTribeState.appUrl) {
                        viewModel.onAppUrlChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TribeTextField("Feed URL", viewModel.createTribeState.feedUrl) {
                        viewModel.onFeedUrlChanged(it)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TribeTextField(
                        label = "Feed Content Type",
                        value = viewModel.createTribeState.feedType,
                        enabled = false,
                        modifier = Modifier.clickable(
                            onClick = if (viewModel.createTribeState.feedUrl.isNotEmpty()) {
                                { showOptionMenu.value = true }
                            } else {
                                { }
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    ) {}
                    CursorDropdownMenu(
                        expanded = showOptionMenu.value,
                        onDismissRequest = { showOptionMenu.value = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.inversePrimary).clip(
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                            onClick = {
                                viewModel.onFeedTypeChanged(FeedType.Podcast)
                                showOptionMenu.value = false
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Podcast", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                            }
                        }
                        DropdownMenuItem(
                            modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                            onClick = {
                                viewModel.onFeedTypeChanged(FeedType.Video)
                                showOptionMenu.value = false
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Video", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                            }
                        }
                        DropdownMenuItem(
                            modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                            onClick = {
                                viewModel.onFeedTypeChanged(FeedType.Newsletter)
                                showOptionMenu.value = false
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Newsletter", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Text(
                                text = "List on ",
                                fontSize = 15.sp,
                                fontFamily = Roboto,
                                fontWeight = FontWeight.W400,
                                color = Color.White,
                            )
                            Text(
                                text = "tribes.sphinx.chat?",
                                fontSize = 15.sp,
                                fontFamily = Roboto,
                                fontWeight = FontWeight.W400,
                                color = tribe_hyperlink,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    openWebpage(URL(" https://tribes.sphinx.chat"))
                                }
                            )
                        }
                        Switch(
                            checked = !viewModel.createTribeState.unlisted,
                            onCheckedChange = { viewModel.onUnlistedChanged(!it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                checkedThumbColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Approve each membership request?",
                            fontSize = 15.sp,
                            fontFamily = Roboto,
                            fontWeight = FontWeight.W400,
                            color = Color.White,
                        )
                        Switch(
                            checked = viewModel.createTribeState.private,
                            onCheckedChange = { viewModel.onPrivateChanged(it) },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                checkedThumbColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(start = 40.dp, end = 40.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                if (viewModel.createTribeState.saveTribeResponse is LoadResponse.Loading) {
                    CircularProgressIndicator(
                        Modifier.padding(20.dp).size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                CommonButton(
                    text = if (chatId != null) "Save" else "Create Tribe",
                    enabled = viewModel.createTribeState.buttonEnabled
                ) {
                    viewModel.saveTribe()
                }
            }
            if (tagPopupState) {
                SelectTagPopup(viewModel) {
                    tagPopupState = false
                }
            }
        }
    }
}

@Composable
fun SelectTagPopup(viewModel: CreateTribeViewModel, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { onClick.invoke() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(color = Color.Black.copy(0.4f))
    ) {
        Column(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth(0.8f)
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant, shape = RoundedCornerShape(10.dp))
                .clickable(
                    onClick = {},
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            Spacer(Modifier.height(8.dp))
            TagRow(0, viewModel)
            TagRow(1, viewModel)
            TagRow(2, viewModel)
            TagRow(3, viewModel)
            TagRow(4, viewModel)
            TagRow(5, viewModel)
            TagRow(6, viewModel)
            TagRow(7, viewModel)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun TagRow(position: Int, viewModel: CreateTribeViewModel) {
    var selected by remember { mutableStateOf(viewModel.tribeTagListState.value[position].isSelected) }
    Box(
        modifier = Modifier.padding(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth().background(
                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(percent = 50)
                )
                .padding(8.dp)
                .clickable(
                    onClick = {
                        selected = !selected
                        viewModel.changeSelectTag(position)
                        viewModel.setTagListState()
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                painter = imageResource(viewModel.tribeTagListState.value[position].image),
                contentDescription = "icon",
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = viewModel.tribeTagListState.value[position].name,
                fontSize = 14.sp,
                fontFamily = Roboto,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

fun openWebpage(uri: URI?): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

fun openWebpage(url: URL): Boolean {
    try {
        return openWebpage(url.toURI())
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
    return false
}