package chat.sphinx.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chat.sphinx.platform.imageResource

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
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = imageResource(Res.drawable.sphinx_logo),
                contentDescription = "Sphinx Logo",
                modifier = Modifier.height(114.dp),
                contentScale = ContentScale.FillHeight
            )
            Spacer(
                modifier = Modifier.height(150.dp)
            )
            Image(
                painter = imageResource(Res.drawable.sphinx_label),
                contentDescription = "Sphinx label",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.paddingFromBaseline(top = 16.dp).width(187.dp)
            )
        }
    }
}