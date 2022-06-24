package views



import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.unit.dp



@Composable
fun LoadingShimmerEffect(effect:@Composable (Brush)->Unit){

    //These colors will be used on the brush. The lightest color should be in the middle

    val gradient = listOf(
        androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.9f), //darker grey (90% opacity)
        androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.3f), //lighter grey (30% opacity)
        androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.9f)
    )

    val transition = rememberInfiniteTransition() // animate infinite times

    val translateAnimation = transition.animateFloat( //animate the transition
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // duration for the animation
                easing = FastOutLinearInEasing
            )
        )
    )
    val brush = linearGradient(
        colors = gradient,
        start = Offset(200f, 200f),
        end = Offset(x = translateAnimation.value,
            y = translateAnimation.value)
    )
    effect(brush)
}

@Composable
fun ShimmerGridItem(brush: Brush) {
    Row(modifier = androidx.compose.ui.Modifier
        .fillMaxSize()
        .padding(all = 2.dp),) {

        Spacer(modifier =  androidx.compose.ui.Modifier
            .size(35.dp)
            .clip(CircleShape)
            .background(brush)
        )
        Spacer(modifier =  androidx.compose.ui.Modifier.width(10.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Spacer(modifier =  androidx.compose.ui.Modifier
                .height(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth(fraction = 0.5f)
                .background(brush)
            )

            Spacer(modifier =  androidx.compose.ui.Modifier.height(5.dp)) //creates an empty space between
            Spacer(modifier =  androidx.compose.ui.Modifier
                .height(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth(fraction = 0.7f)
                .background(brush)
            )

//            Spacer(modifier =  androidx.compose.ui.Modifier.height(10.dp)) //creates an empty space between
//            Spacer(modifier =  androidx.compose.ui.Modifier
//                .height(20.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .fillMaxWidth(fraction = 0.9f)
//                .background(brush))
        }
    }
}

@Composable
fun ShimmerCircleAvatar(brush: Brush) {
    Row(modifier = androidx.compose.ui.Modifier
        .fillMaxSize()
        .padding(all = 2.dp),) {

        Spacer(modifier =  androidx.compose.ui.Modifier
            .size(35.dp)
            .clip(CircleShape)
            .background(brush)
        )
    }
}