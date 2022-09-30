package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import chat.sphinx.wrapper.message.FeedBoost
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.lightning.asFormattedString
import theme.primary_green


@Composable
fun PodcastBoost(
    feedBoost: FeedBoost,
){
    Row(
        modifier = Modifier.height(50.dp).wrapContentWidth().padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = "Boost!",
            fontWeight = FontWeight.W300,
            fontFamily = Roboto,
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier.wrapContentSize(),
            text = feedBoost.amount.asFormattedString(),
            fontWeight = FontWeight.Bold,
            fontFamily = Roboto,
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(32.dp))
        Box(
            modifier = Modifier.clip(CircleShape)
                .background(primary_green)
                .size(28.dp)
        ) {
            Image(
                painter = imageResource(Res.drawable.ic_boost),
                contentDescription = "Boost",
                modifier = Modifier.fillMaxWidth().padding(2.dp),
                contentScale = ContentScale.Inside
            )
        }
    }
}
