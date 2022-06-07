package utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedContainer(fromTopToBottom:Int=0,fromBottomToTop:Int=0,fromRightToLeft:Int=0,delayTime:Int=10,content:@Composable ()->Unit) {
    var visible by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    LaunchedEffect(key1 = "test", block ={
        delay(delayTime.toLong())
        visible=true
    } )
    AnimatedVisibility(visible = visible,
        enter = if(fromRightToLeft!=0) slideInHorizontally{
            with(density) {
                -fromRightToLeft.dp.roundToPx()
            }
        } else slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) {
                if(fromTopToBottom!=0)
                    fromTopToBottom.dp.roundToPx()

                else -fromBottomToTop.dp.roundToPx()

            }
        } + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),){
            content()
    }
}