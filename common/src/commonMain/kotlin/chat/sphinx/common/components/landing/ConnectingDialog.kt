package chat.sphinx.common.components.landing

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import kotlin.math.roundToInt
@Composable
fun ConnectingDialog() {
    var connectingContent = ""
    val infiniteConnectingTransition = rememberInfiniteTransition()
    val alfa by infiniteConnectingTransition.animateFloat(initialValue = 0f, targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1.5f at 1000
            }
        )
    )
    when (alfa.roundToInt()) {
        0 -> {
            connectingContent = "CONNECTING"
        }
        1 -> {
            connectingContent = "CONNECTING."
        }
        2 -> {
            connectingContent = "CONNECTING.."
        }
        3 -> {
            connectingContent = "CONNECTING..."
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,

        ) {

        Column {

            Image(
                painter = imageResource(Res.drawable.connection_image), contentDescription = "connecting",
                modifier = Modifier.width(150.dp)
                    .height(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(38.dp))
            Text(
                text = connectingContent,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.W700,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                // textAlign = TextAlign.Center
            )
        }
    }

}