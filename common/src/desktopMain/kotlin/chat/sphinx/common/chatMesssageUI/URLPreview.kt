package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.wrapper.PhotoUrl

@Composable
actual fun URLPreview() {
    Column (modifier = Modifier.padding(12.dp)){
        Divider(color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Column {
                Row() {
                    PhotoUrlImage(PhotoUrl("https://picsum.photos/200/300?random=1"), modifier = Modifier.size(20.dp).clip(
                        RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Infobase", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                }
                Text("This is sub title", color = MaterialTheme.colorScheme.tertiary, fontSize = 10.sp)
                Text("https://www.google.com", color = MaterialTheme.colorScheme.tertiary,fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            PhotoUrlImage(PhotoUrl("https://picsum.photos/200/300?random=1"), modifier = Modifier.size(80.dp).clip(
                RoundedCornerShape(2.dp)))
        }
    }
}