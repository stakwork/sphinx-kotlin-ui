package chat.sphinx.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.PhotoUrl
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

@Composable
fun PhotoUrlImage(
    photoUrl: PhotoUrl?,
    modifier: Modifier = Modifier,
) {
    if (photoUrl != null) {
        val photoUrlResource = lazyPainterResource(
            data = photoUrl.value
        )
        KamelImage(
            resource = photoUrlResource,
            contentDescription = "avatar",
            onLoading = {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            onFailure = {
                Image(
                    painter = imageResource(Res.drawable.sphinx_logo),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,            // crop the image if it's not a square
                    modifier = modifier
                        .border(2.dp, Color.Red, CircleShape)   // add a border (optional)
                )
            },
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = modifier
        )
    } else {
        // Show a default
        Image(
            painter = imageResource(Res.drawable.sphinx_logo),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = modifier
        )
    }
}