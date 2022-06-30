package chat.sphinx.common.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
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
import views.LoadingShimmerEffect
import views.ShimmerGridItem

val TWO_HOURS_IN_SECONDS = 7_200

@Composable
fun PhotoUrlImage(
    photoUrl: PhotoUrl?,
    modifier: Modifier = Modifier,
    effect: @Composable() (() -> Unit?)? = null,
    firstNameLetter: String? = null,
    color: Color? = null
) {
    val kamelConfig = KamelConfig { // TODO: Make this multiplatform...
        takeFrom(KamelConfig.Default)
        imageBitmapCacheSize = 1000
        httpFetcher {
            defaultRequest {
                cacheControl(
                    CacheControl.MaxAge(
                        maxAgeSeconds = TWO_HOURS_IN_SECONDS
                    )
                )
            }
        }
    }
    if (photoUrl != null) {
        CompositionLocalProvider(LocalKamelConfig provides kamelConfig) {

            val photoUrlResource = lazyPainterResource(
                data = photoUrl.value
            )

            KamelImage(
                resource = photoUrlResource,
                contentDescription = "avatar",
                onLoading = {

                    if (effect != null) {
                        effect()
                    } else {
                        Image(
                            modifier = modifier,
                            painter = imageResource(Res.drawable.profile_avatar),
                            contentDescription = "avatar",
                            contentScale = ContentScale.Crop
                        )
                    }

                },
                onFailure = {
                    Image(
                        modifier = modifier,
                        painter = imageResource(Res.drawable.profile_avatar),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop
                    )
                },
                contentScale = ContentScale.Crop,
                modifier = modifier,
                crossfade = false
            )
        }
    } else {
        if (firstNameLetter == null) {
            Image(
                modifier = modifier,
                painter = imageResource(Res.drawable.profile_avatar),
                contentDescription = "avatar",
                contentScale = ContentScale.Crop
            )
        } else {
            color?.let { Modifier.background(it, shape = CircleShape).size(30.dp) }?.let {
                Box(modifier = it, contentAlignment = Alignment.Center) {
                    Text(text = firstNameLetter, color = Color.White)
                }
            }
        }

    }
}