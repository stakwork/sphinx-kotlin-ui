package chat.sphinx.common.state

import com.google.zxing.common.BitMatrix

data class ContactQRCodeState(
    val pubKey: String = "",
    val bitMatrix: BitMatrix? = null
)
