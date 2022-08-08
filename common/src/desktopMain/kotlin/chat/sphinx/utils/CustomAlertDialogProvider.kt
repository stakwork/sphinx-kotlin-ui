package chat.sphinx.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialogProvider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider

@ExperimentalMaterialApi
object CustomAlertDialogProvider : AlertDialogProvider {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun AlertDialog(
        onDismissRequest: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset = IntOffset(600,0)
            },
            focusable = true,
            onDismissRequest = onDismissRequest,
            onKeyEvent = {
                if (it.key == Key.Escape) {
                    onDismissRequest()
                    true
                } else {
                    false
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f).fillMaxHeight().background(Color.Black.copy(alpha = 0.2f))
                    .pointerInput(onDismissRequest) {
                        detectTapGestures(onPress = { onDismissRequest() })
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(elevation = 24.dp, shape = RoundedCornerShape(5)) {
                    content()
                }
            }
        }
    }
}