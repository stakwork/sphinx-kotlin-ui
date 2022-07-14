package chat.sphinx.common.components

import androidx.compose.runtime.Composable
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.resourcesFetcher

@Composable
actual fun kamelFileConfig(): KamelConfig = KamelConfig { // TODO: Make this multiplatform...
    takeFrom(KamelConfig.Default)
    resourcesFetcher()
}