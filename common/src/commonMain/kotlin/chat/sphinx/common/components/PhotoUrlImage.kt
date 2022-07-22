package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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

val kamelConfig = KamelConfig { // TODO: Make this multiplatform...
    takeFrom(KamelConfig.Default)
    imageBitmapCacheSize = 1000
    httpFetcher {
        defaultRequest {
            cacheControl(
                CacheControl.MaxAge(
                    maxAgeSeconds = 7_200
                )
            )
        }
    }
}

@Composable
fun PhotoUrlImage(
    photoUrl: PhotoUrl?,
    modifier: Modifier = Modifier,
    effect: @Composable (() -> Unit?)? = null,
    firstNameLetter: String? = null,
    color: Color? = null,
    fontSize: Int? = null
) {
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
                        InitialsCircleOrAvatar(
                            modifier,
                            firstNameLetter,
                            color,
                            fontSize
                        )
                    }

                },
                onFailure = {
                    InitialsCircleOrAvatar(
                        modifier,
                        firstNameLetter,
                        color,
                        fontSize
                    )
                },
                contentScale = ContentScale.Crop,
                modifier = modifier,
                crossfade = false
            )
        }
    } else {
        InitialsCircleOrAvatar(
            modifier,
            firstNameLetter,
            color,
            fontSize
        )
    }
}

@Composable
fun InitialsCircleOrAvatar(
    modifier: Modifier = Modifier,
    firstNameLetter: String? = null,
    color: Color? = null,
    fontSize: Int? = null
) {
    if (firstNameLetter == null || color == null) {
        Image(
            modifier = modifier,
            painter = imageResource(Res.drawable.profile_avatar),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop
        )
    } else {
        color?.let { modifier.background(it, shape = CircleShape) }?.let {
            Box(modifier = it, contentAlignment = Alignment.Center) {
                Text(
                    text = firstNameLetter,
                    color = Color.White,
                    fontFamily = Roboto,
                    fontSize = fontSize?.sp ?: TextUnit.Unspecified
                )
            }
        }
    }
}