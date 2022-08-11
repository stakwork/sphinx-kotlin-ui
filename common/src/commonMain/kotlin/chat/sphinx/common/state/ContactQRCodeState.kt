package chat.sphinx.common.state

import com.google.zxing.common.BitMatrix

data class ContactQRCodeState(
    val viewTitle: String = "QR Code",
    val string: String = "",
    val bitMatrix: BitMatrix? = null
)
