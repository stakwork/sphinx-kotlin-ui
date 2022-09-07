import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.components.landing.photoTestUrl
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.PhotoUrl
import theme.lightning_network_point
import theme.lightning_network_point_alpha

@Composable
fun OnBoardLightningScreen(isWelcome: Boolean, isEndScreen: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = imageResource(Res.drawable.lightning_network),
                contentDescription = "Lightning Network",
                modifier = Modifier.fillMaxSize().padding(
                    end = if (isWelcome) 40.dp else 0.dp
                ),
                contentScale = if (isWelcome) ContentScale.Fit else ContentScale.FillHeight,
                alpha = 0.55f
            )
            Canvas(modifier = Modifier.size(12.dp),
                onDraw = {
                    drawCircle(color = lightning_network_point)
                }
            )

            val targetAlphaState = remember { mutableStateOf(0f) }
            val targetRadiusState = remember { mutableStateOf(0f) }

            val animatedAlphaState = animateFloatAsState(
                targetValue = targetAlphaState.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1200),
                    repeatMode = RepeatMode.Reverse
                )
            )
            val animatedRadiusState = animateFloatAsState(
                targetValue = targetRadiusState.value,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1200),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Canvas(modifier = Modifier.size(12.dp), onDraw = {
                drawCircle(
                    color = lightning_network_point_alpha,
                    radius = animatedRadiusState.value,
                    alpha = animatedAlphaState.value
                )
                targetAlphaState.value = 1f
                targetRadiusState.value = 40f
            })
        }
    }
    if (isEndScreen) {
        ProfileDialogBox(photoTestUrl)

    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().padding(top = 80.dp)
        ) {
            Text(
                text = "You are now on the\n" +
                        " lightning network!",
                fontSize = 30.sp,
                maxLines = 2,
                color = Color.White,
                fontFamily = Roboto,
                fontWeight = FontWeight.Light,
            )
        }
    }
    if (isWelcome) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
        ) {
            Box(modifier = Modifier.height(48.dp).width(259.dp)) {
                CommonButton(text = "Continue", true, endIcon = Icons.Default.ArrowForward) {
                    LandingScreenState.screenState(LandingScreenType.OnBoardSignUp)
                }
            }
        }
    }
}
@Composable
private fun ProfileDialogBox(photoUrl: PhotoUrl){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = 200.dp)
    ) {
        Card(
            modifier = Modifier.width(236.dp).height(87.dp),
            shape = RoundedCornerShape(61.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 6.dp, top = 3.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoUrlImage(
                    photoUrl = photoUrl,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = "Wayne Michaels",
                    fontSize = 20.sp,
                    maxLines = 2,
                    color = Color.Black,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = 60.dp)
    ) {
        Canvas(modifier = Modifier.size(20.dp).padding(bottom = 200.dp), onDraw = {
            val size = 20.dp.toPx()

            rotate(degrees = -180f) {
                val trianglePath = Path().apply {
                    moveTo(size / 2f, 0f)
                    lineTo(size, size)
                    lineTo(0f, size)
                }
                drawPath(
                    color = Color.White,
                    path = trianglePath
                )
            }
        })
    }
}