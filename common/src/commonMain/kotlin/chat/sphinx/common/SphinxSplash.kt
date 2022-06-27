package chat.sphinx.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chat.sphinx.platform.imageResource
import com.example.compose.md_theme_dark_purple

@Composable
fun SphinxSplash() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = imageResource(Res.drawable.splash_background),
            contentDescription = "Sphinx Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Image(
            painter = imageResource(Res.drawable.sphinx_logo),
            contentDescription = "Sphinx Logo",
            modifier = Modifier
                .height(114.dp),
            contentScale = ContentScale.FillHeight
        )
        //TODO Add Sphinx label on Splash
//            Spacer(
//                modifier = Modifier.height(150.dp)
//            )
//            Image(
//                painter = imageResource(Res.drawable.sphinx_label),
//                contentDescription = "Sphinx Label",
//                modifier = Modifier
//                    .height(187.dp)
//                    .width(22.dp),
//                contentScale = ContentScale.Crop
//            )
    }
}