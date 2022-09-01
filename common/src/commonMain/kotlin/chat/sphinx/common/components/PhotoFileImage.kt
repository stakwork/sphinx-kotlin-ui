package chat.sphinx.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.core.config.KamelConfig
import io.kamel.image.KamelImage
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.lazyPainterResource
import okio.Path

@Composable
expect fun kamelFileConfig(): KamelConfig

@Composable
fun PhotoFileImage(
    photoFilepath: Path,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Inside,
    effect: @Composable() (() -> Unit?)? = null
) {
    val kamelConfig = kamelFileConfig()

    CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {

        val photoUrlResource = lazyPainterResource(
            data = photoFilepath.toFile()
        )

        KamelImage(
            resource = photoUrlResource,
            contentDescription = "image file",
            onLoading = {

                if (effect != null) {
                    effect()
                } else {
                    Box(modifier=Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center){
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

            },
            onFailure = {
                Icon(
                    Icons.Default.Error,
                    "Image Load Error",
                    tint = Color.Red,
                    modifier = modifier
                )
            },
            contentScale = contentScale,
            modifier = modifier,
            crossfade = false
        )
    }
}