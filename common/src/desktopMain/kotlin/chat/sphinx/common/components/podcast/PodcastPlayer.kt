package chat.sphinx.common.components.podcast

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.DesktopResource
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.util.getInitials

@Composable
actual fun PodcastPlayer(exitApplication: () -> Unit) {
    val sphinxIcon = imageResource(DesktopResource.drawable.sphinx_icon)
    Window(
        onCloseRequest = { exitApplication() },
        title = "Sphinx",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(400, 700)
        ),
        undecorated = false,
        icon = sphinxIcon,
    ) {
        Surface(
            elevation = 10.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            CollapsingEffectScreen()
        }
    }

}

@Composable
fun CollapsingEffectScreen() {
    val lazyListState = rememberLazyListState()
    var scrolledY = 0f
    var previousOffset = 0
    val satsPerMinute = remember { mutableStateOf(0.1f) }
    LazyColumn(
        Modifier.fillMaxSize(),
        lazyListState,
    ) {
        item {
            PhotoUrlImage(
                photoUrl = PhotoUrl("https://img.freepik.com/free-vector/podcast-poster-template_47987-2202.jpg"),
                modifier = Modifier
                    .graphicsLayer {
                        scrolledY += lazyListState.firstVisibleItemScrollOffset - previousOffset
                        translationY = scrolledY * 0.5f
                        previousOffset = lazyListState.firstVisibleItemScrollOffset
                    }
                    .height(240.dp)
                    .fillMaxWidth(),
            )

        }
        item {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Spacer(modifier = Modifier.height(16.dp))

//                Spacer(modifier = Modifier.height(16.dp))
                Box() {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Podcast: sats per minute",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600
                        )
                        Text(
                            "20",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600
                        )

                    }
                    Box(modifier = Modifier.offset(y = 15.dp).padding(horizontal = 8.dp)) {
                        Slider(
                            onValueChange = {
                                satsPerMinute.value = it
                            }, value = satsPerMinute.value, colors = SliderDefaults.colors(
                                activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                thumbColor = Color.Gray, inactiveTrackColor = MaterialTheme.colorScheme.onBackground,
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

            }
            val padding = 4.dp
            val density = LocalDensity.current
            Surface(
                shape = RectangleShape,
                color = Color.White,
                elevation = 10.dp,
                modifier = Modifier
                    .padding(bottom = padding)
                    .drawWithContent {
                        val paddingPx = with(density) { padding.toPx() }
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.width ,
                            bottom = size.height + paddingPx
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.onSecondaryContainer).height(290.dp)
                            .padding(horizontal = 16.dp)
                        , horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Test - Podcast",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W600
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box {
                            Slider(
                                onValueChange = {
                                    satsPerMinute.value = it
                                }, value = satsPerMinute.value, colors = SliderDefaults.colors(
                                    activeTrackColor = MaterialTheme.colorScheme.inverseSurface,
                                    thumbColor = MaterialTheme.colorScheme.inverseSurface, inactiveTrackColor = Color(0xFF4D829CB9),
                                )
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).offset(y = 35.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "00:00:00",
                                    color = MaterialTheme.colorScheme.inverseSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400, fontFamily = Roboto
                                )
                                Text(
                                    "00:00:00",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400
                                )

                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        PlaybackSpeed()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = imageResource(Res.drawable.ic_share_podcast), contentDescription = null, modifier = Modifier.size(45.dp), tint = MaterialTheme.colorScheme.onBackground)
                            Icon(painter = imageResource(Res.drawable.ic_podcast_back_15), contentDescription = null, modifier = Modifier.size(40.dp),tint = MaterialTheme.colorScheme.onBackground)
                            IconButton(onClick = {}, modifier = Modifier.size(58.dp).background(MaterialTheme.colorScheme.inverseSurface,
                                RoundedCornerShape(100)
                            )){
                                Icon(Icons.Default.PlayArrow, tint = MaterialTheme.colorScheme.tertiary, contentDescription = null, modifier = Modifier.size(30.dp))
                            }
                            Icon(painter = imageResource(Res.drawable.ic_podcast_forward_30), contentDescription = null, modifier = Modifier.size(40.dp),tint = MaterialTheme.colorScheme.onBackground)
                            Box(contentAlignment = Alignment.Center) {
                                Box(modifier = Modifier.size(33.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(100)
                                )) {

                                }
                                Image(painter = imageResource(Res.drawable.ic_boost_green), contentDescription = null, modifier = Modifier.clip(
                                    RoundedCornerShape(100)
                                ).background(Color.Transparent, RoundedCornerShape(100)).size(31.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)){
                            Text("EPISODES", fontWeight = FontWeight.W400, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, fontFamily = SphinxFonts.montserratFamily)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("12", fontWeight = FontWeight.W400, color = MaterialTheme.colorScheme.secondary,fontSize = 13.sp, fontFamily = SphinxFonts.montserratFamily)
                        }
                    }
                }
        }
        items(count = 100) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                // Below Icon is used for playing the podcast
//                Icon(Icons.Default.PlayArrow, tint = MaterialTheme.colorScheme.inverseSurface, contentDescription = null, modifier = Modifier.size(30.dp))
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer).padding(vertical = 16.dp)) {
                    Row ( horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){

                        Spacer(modifier = Modifier.width(8.dp))
                        PhotoUrlImage(
                            photoUrl = PhotoUrl("https://picsum.photos/seed/picsum/200/300"),
                            modifier = Modifier
                                .size(40.dp),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("This is test title", fontSize = 14.sp, color = MaterialTheme.colorScheme.tertiary )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {}){
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground,)
                        }
                        // Below Icon is used for showing the progress of download
//                    CircularProgressIndicator(modifier = Modifier.size(25.dp), color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 0.3.dp, modifier =Modifier.padding(start = 8.dp, end = 38.dp))

                }

            }
        }
    }
}

@Composable
fun PlaybackSpeed(){
    Card(backgroundColor =androidx.compose.material3.MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(2.dp)) {
            Spacer(modifier = Modifier.width(4.dp))
            Text("1x", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.height(8.dp).width(16.dp).background(Color(0xff2F60CE),
                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                ))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary,modifier = Modifier.height(8.dp).width(16.dp).background(Color(0xff2F60CE),
                    RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                ))
            }
        }
    }
}

private fun Modifier.bottomElevation(): Modifier = this.then(Modifier.drawWithContent {
    val paddingPx = 8.dp.toPx()
    clipRect(
        left = 0f,
        top = 0f,
        right = size.width,
        bottom = size.height + paddingPx
    ) {
        this@drawWithContent.drawContent()
    }
})