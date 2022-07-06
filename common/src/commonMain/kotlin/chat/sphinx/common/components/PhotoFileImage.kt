package chat.sphinx.common.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.PhotoUrl
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig
import io.kamel.image.lazyPainterResource
import io.ktor.client.features.*
//import io.ktor.client.plugins.*
import io.ktor.http.*
import okio.Path
import views.LoadingShimmerEffect
import views.ShimmerGridItem

@Composable
expect fun kamelFileConfig(): KamelConfig

@Composable
fun PhotoFileImage(
    photoFilepath: Path,
    modifier: Modifier = Modifier,
    effect: @Composable() (() -> Unit?)? = null
) {
    val kamelConfig = kamelFileConfig()

    CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {

        val photoUrlResource = lazyPainterResource(
            data = photoFilepath.toFile()
        )

        KamelImage(
            resource = photoUrlResource,
            contentDescription = "avatar",
            onLoading = {

                if (effect != null) {
                    effect()
                } else {
                    Icon(
                        Icons.Default.Attachment,
                        "Image Attachment",
                        tint = Color.Red,
                        modifier = modifier
                    )
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
            contentScale = ContentScale.Crop,
            modifier = modifier,
            crossfade = false
        )
    }
}