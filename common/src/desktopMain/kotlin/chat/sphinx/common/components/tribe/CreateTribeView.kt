package chat.sphinx.common.components.tribe

import CommonButton
import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.chat.CreateTribeViewModel
import chat.sphinx.concepts.link_preview.model.toPhotoUrl
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import okio.Path
import theme.tribe_hyperlink
import utils.deduceMediaType
import java.awt.Desktop
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

@Composable
fun CreateTribeView(dashboardViewModel: DashboardViewModel) {
    var isOpen by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val viewModel = CreateTribeViewModel()

    if (isOpen) {
        Window(
            onCloseRequest = { dashboardViewModel.toggleCreateTribeWindow(false) },
            title = "Create Tribe",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 620)
            )
        ) {
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
                        TribeTextField(
                            "Image",
                            viewModel.createTribeState.imgUrl,
                            Modifier.padding(end = 50.dp),
                            false
                        ) {}
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
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
                            val path = viewModel.createTribeState.img
                            if (path != null) {
                                PhotoFileImage(
                                    photoFilepath = path,
                                    modifier = Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            onImageClick.invoke()
                                        }
                                )
                            }
                            else {
                                PhotoUrlImage(
                                    photoUrl = PhotoUrl("https://example.com"),
                                    modifier = Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            onImageClick.invoke()
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
                    TribeTextField("Tags", "") {}
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
                    val timeToStake = viewModel.createTribeState.escrowMillis?.let {
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
                    TribeTextField("Feed Content Type", "") {}
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
                            checked = viewModel.createTribeState.unlisted,
                            onCheckedChange = { viewModel.onUnlistedChanged(it) },
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
                verticalArrangement = Arrangement.Bottom
            )
            {
                CommonButton("Create Tribe") {}
            }
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