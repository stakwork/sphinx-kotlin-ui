package chat.sphinx.common.components.tribe

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl
import theme.light_divider

@Composable
fun LeaderboardUI(dashboardViewModel: DashboardViewModel) {
    var isOpen by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val viewModel = remember { dashboardViewModel }

    if (isOpen) {
        Window(
            onCloseRequest = { dashboardViewModel.toggleLeaderboardWindow(false) },
            title = "Leaderboard",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 620)
            )
        ) {
            val listState = rememberLazyListState()

            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().height(260.dp).background(MaterialTheme.colorScheme.onSurfaceVariant),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SmallProfile("2")
                        Column(
                            modifier = Modifier.fillMaxHeight().wrapContentWidth(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(24.dp))
                            Box(
                                modifier = Modifier.height(30.dp).width(45.dp),
                                contentAlignment = Alignment.BottomCenter
                            ){
                                Image(
                                    painter = imageResource(Res.drawable.ic_crown),
                                    contentDescription = "crown icon",
                                    contentScale = ContentScale.Crop
                                )
                            }
                            BigProfile("1")
                        }
                        SmallProfile("3")
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Box(
                    modifier = Modifier.fillMaxSize()

                ) {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(count = 16) {
                            UsersRow()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SmallProfile(position: String) {
    Box(
        Modifier
            .width(120.dp)
            .height(148.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.wrapContentSize().padding(16.dp)
        ) {
            PhotoUrlImage(
                PhotoUrl("empty"),
                modifier = Modifier.border(BorderStroke(3.dp, MaterialTheme.colorScheme.tertiary), shape = CircleShape)
            )
            Box(
                modifier = Modifier.clip(CircleShape).size(24.dp).background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = position,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SphinxFonts.montserratFamily,
                    fontSize = 14.sp
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = "Thomas",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 17.sp
                )
                Spacer(Modifier.width(6.dp))
                Image(
                    // TODO add if statement to include ic_polygon_down and handle down position
                    painter = imageResource(Res.drawable.ic_polygon_up),
                    contentDescription = "position up and down",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BigProfile(position: String) {
    Box(
        Modifier
            .width(146.dp)
            .height(174.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.wrapContentSize().padding(16.dp)
        ) {
            PhotoUrlImage(
                PhotoUrl("empty"),
                modifier = Modifier.border(BorderStroke(4.dp, MaterialTheme.colorScheme.tertiary), shape = CircleShape)
            )
            Box(
                modifier = Modifier.clip(CircleShape).size(28.dp).background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SphinxFonts.montserratFamily,
                    fontSize = 18.sp
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = "Thomas",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(Modifier.width(6.dp))
                Image(
                    painter = imageResource(Res.drawable.ic_polygon_up),
                    contentDescription = "position up",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun UsersRow(){
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.weight(2f).height(64.dp).padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoUrlImage(PhotoUrl("empty"))
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Thomas",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
        Column (
            modifier = Modifier.weight(1f).height(64.dp).padding(end = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1662",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(42.dp))
                Text(
                    text = "1",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = Roboto,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    // TODO add if statement to include ic_polygon_down and handle down position
                    painter = imageResource(Res.drawable.ic_polygon_up),
                    contentDescription = "position up and down",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
    Divider(modifier = Modifier.fillMaxWidth().padding(start = 68.dp, end = 20.dp), color = light_divider)
}
