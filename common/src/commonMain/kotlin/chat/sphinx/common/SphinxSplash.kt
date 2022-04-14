package chat.sphinx.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import chat.sphinx.platform.imageResource

@Composable
fun SphinxSplash() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(SolidColor(Color.Gray), alpha = 0.50f)
    ) {
        Image(
            painter = imageResource(Res.drawable.sphinx_logo),
            contentDescription = "Sphinx Logo",
            modifier = Modifier.fillMaxSize()
        )
    }
}