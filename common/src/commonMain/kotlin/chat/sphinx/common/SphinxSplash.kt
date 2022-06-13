package chat.sphinx.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import chat.sphinx.platform.imageResource
import com.example.compose.md_theme_dark_purple

@Composable
fun SphinxSplash() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.linearGradient(
                start = Offset.Infinite,
                end = Offset.Zero,
                colors = listOf(
                    MaterialTheme.colors.secondary,
                    MaterialTheme.colors.secondary,
                    md_theme_dark_purple,

                )
            ))
    ) {
        Image(
            painter = imageResource(Res.drawable.sphinx_logo),
            contentDescription = "Sphinx Logo",
            modifier = Modifier.height(150.dp).width(150.dp)
        )
    }
}