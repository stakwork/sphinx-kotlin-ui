package chat.sphinx.common.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
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
import io.ktor.http.*
import views.LoadingShimmerEffect

val TWO_HOURS_IN_SECONDS = 7_200

@Composable
fun PhotoUrlImage(
    photoUrl: PhotoUrl?,
    modifier: Modifier = Modifier,
) {
    val kamelConfig = KamelConfig { // TODO: Make this multiplatform...
        takeFrom(KamelConfig.Default)

        httpFetcher {
            defaultRequest {
                cacheControl(
                    CacheControl.MaxAge(
                        maxAgeSeconds = TWO_HOURS_IN_SECONDS
                    )
                )
            }
            // TODO: Setup a httpClient that integrates the tor proxy here...
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
                 animationSpec = tween(),
                 onLoading = {
                     Image(
                         modifier = modifier,
                         painter = imageResource(Res.drawable.profile_avatar),
                         contentDescription = "avatar",
                         contentScale = ContentScale.Crop
                     )
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
                 modifier = modifier
             )
        }
    } else {
        Image(
            modifier = modifier,
            painter = imageResource(Res.drawable.profile_avatar),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop
        )
    }
}