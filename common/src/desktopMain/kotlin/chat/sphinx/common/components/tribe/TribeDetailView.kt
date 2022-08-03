package chat.sphinx.common.components.tribe

import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.Res
import chat.sphinx.common.components.OptionItem
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.chat.KebabMenu
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl
import com.example.compose.badge_red

@Composable
actual fun TribeDetailView(dashboardViewModel: DashboardViewModel) {
    Window(
        onCloseRequest = {
            dashboardViewModel.toggleTribeDetailWindow(false)
        },
        title = "Sphinx",

        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(400, 500)
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant).padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            TopHeader()
            Spacer(modifier = Modifier.height(16.dp))
            TribeTextField("Alias", "Sachin") {}
            Box (){
                TribeTextField("Profile Picture", "https://www.google.co.in") {}
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp)){
                    PhotoUrlImage(
                        PhotoUrl("https://picsum.photos/200/300"), modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape).align(Alignment.TopEnd)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Privacy Setting",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 10.dp)
                )
                IconButton(onClick = {}, modifier = Modifier.size(20.dp)) {
                    Icon(
                        Icons.Outlined.Help,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Select PIN", fontSize = 17.sp, color = MaterialTheme.colorScheme.tertiary)
                Tabs()
            }
        }
    }
}

@Composable
fun TribeTextField(placeholder: String, value: String, onTextChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        androidx.compose.material.Text(
            text = placeholder,
            fontSize = 12.sp,
            fontFamily = Roboto,
            color = MaterialTheme.colorScheme.onBackground,
        )
        BasicTextField(
            value = value,
            onValueChange = {
                onTextChange(it)
            },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = Color.White, fontFamily = Roboto),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary)

        )
        Divider(modifier = Modifier.fillMaxWidth().padding(top = 6.dp), color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun TopHeader() {
    val showOptionMenu = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center
    ) {
        Column {
            Spacer(modifier = Modifier.height(5.dp))
            PhotoUrlImage(
                PhotoUrl("https://picsum.photos/200/300"), modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "Tom Podcast",
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Column {
                Text("Created on Wed Jun 01 06:35", color = MaterialTheme.colorScheme.onBackground, fontSize = 11.sp)
                Text(
                    "Price per message: 0 sat -  Amount to stake: 0 sat",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 10.sp
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            KebabMenu(
                contentDescription = "Menu"
            ) {
                showOptionMenu.value = showOptionMenu.value.not()
            }
            CursorDropdownMenu(
                expanded = showOptionMenu.value,

                onDismissRequest = {}, modifier = Modifier.background(MaterialTheme.colorScheme.inversePrimary).clip(
                    RoundedCornerShape(16.dp)
                )
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                DropdownMenuItem(
                    modifier = Modifier.height(40.dp).width(180.dp).clip(RoundedCornerShape(8.dp)),
                    onClick = {
                    }) {
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
                    }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "",
                            tint = badge_red, modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Tribe", color =badge_red, fontSize = 12.sp)
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