package chat.sphinx.common.state

import androidx.compose.ui.graphics.ImageBitmap

data class ContactQRCodeState(
    val pubKey: String = "",
    val qrBitmap: ImageBitmap? = null
)
