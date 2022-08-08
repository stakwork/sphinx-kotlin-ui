package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import okio.Path


@Composable
fun ImageFullScreen(
    fullScreenImageState: MutableState<Path?>
) {
    fullScreenImageState.value?.let { imagePath ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.85f))
                .clickable(enabled = true, onClick = {
                    fullScreenImageState.value = null
                })
        ) {
            ImageFullScreen(imagePath) {
                fullScreenImageState.value = null
            }
        }
    }
}

@Composable
fun ImageFullScreen(
    path: Path,
    callback: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        PhotoFileImage(
            path,
            modifier = Modifier.fillMaxSize().padding(50.dp),
            contentScale = ContentScale.Inside
        )
        Box(
            modifier = Modifier.padding(20.dp).align(Alignment.TopStart)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close fullscreen image view",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(30.dp).clickable(enabled = true, onClick = {
                    callback()
                })
            )
        }
    }
}
