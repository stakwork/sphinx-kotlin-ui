package chat.sphinx.common.components.tribe

import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.chat.JoinTribeViewModel
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.tribe.TribeJoinLink
import kotlinx.coroutines.launch
import utils.deduceMediaType

@Composable
actual fun JoinTribeView(dashboardViewModel: DashboardViewModel, tribeJoinLink: TribeJoinLink?) {

    val viewModel = remember { JoinTribeViewModel() }
    viewModel.loadTribeData(tribeJoinLink)
    val scope = rememberCoroutineScope()


    Window(
        onCloseRequest = {
            dashboardViewModel.toggleJoinTribeWindow(false, null)
        },
        title = "Sphinx",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(400, 800)
        ),
//        icon = sphinxIcon,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant)
                .padding(vertical = 24.dp, horizontal = 16.dp).verticalScroll(
                rememberScrollState()
            ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhotoUrlImage(
                photoUrl = viewModel.joinTribeState.img,
                modifier = Modifier.size(80.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                viewModel.joinTribeState.name,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                viewModel.joinTribeState.description,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                BoxWithStroke("Price to join:", viewModel.joinTribeState.price_to_join, BoxWithStrokeEnums.TOP)
                BoxWithStroke("Price per message:", viewModel.joinTribeState.price_per_message)
                BoxWithStroke("Amount to stake:", viewModel.joinTribeState.escrow_amount)
                BoxWithStroke("Time to stake: (hours)", viewModel.joinTribeState.hourToStake, BoxWithStrokeEnums.BOTTOM
                )
                Spacer(modifier = Modifier.height(24.dp))
                TribeTextField("Alias", viewModel.joinTribeState.userAlias) {
                    viewModel.onAliasTextChanged(it)
                }
                Box(
                    modifier = Modifier
                ) {
                    TribeTextField(
                        "Profile Picture",
                        viewModel.joinTribeState.myPhotoUrl?.value ?: "",
                        Modifier.padding(end = 50.dp),
                        enabled = false
                    ) {}
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp)
                            .wrapContentSize()
                    ) {
                        val onImageClick = {
                            scope.launch {
                                ContentState.sendFilePickerDialog.awaitResult()?.let { path ->
                                    if (path.deduceMediaType().isImage) {
                                        viewModel.onProfilePictureChanged(path)
                                    }
                                }
                            }
                        }
                        PhotoUrlImage(
                            photoUrl = viewModel.joinTribeState.myPhotoUrl,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onImageClick.invoke()
                                }
                        )
                    }
                }
                Box(
                    Modifier.fillMaxWidth().height(40.dp).padding(bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.joinTribeState.status is LoadResponse.Loading) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
                Button(
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth(0.7f).height(45.dp).align(Alignment.CenterHorizontally),
                    onClick = {
                        viewModel.joinTribe()
                    }
                ) {
                    androidx.compose.material.Text(
                        text = "JOIN TRIBE",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.W800,
                        fontFamily = Roboto
                    )
                }
            }
        }
    }
    if (viewModel.joinTribeState.status is Response.Success){
        dashboardViewModel.toggleJoinTribeWindow(false, null)
    }
}

@Composable
fun BoxWithStroke(labelName:String,value:String,boxWithStrokeEnums: BoxWithStrokeEnums=BoxWithStrokeEnums.NONE) {
    val roundedCornerShape=when(boxWithStrokeEnums){
        BoxWithStrokeEnums.TOP -> RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp)
        BoxWithStrokeEnums.BOTTOM -> RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp)
        BoxWithStrokeEnums.NONE -> RoundedCornerShape(0.dp)
    }
    Card (border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onBackground), shape = roundedCornerShape, backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant){
        Row (modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Text(labelName, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
        }
    }
}
 enum class BoxWithStrokeEnums{
    TOP,
    BOTTOM,
    NONE
}
