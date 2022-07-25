package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ContactQRCodeState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.wrapper.util.isValidBech32
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch

class QRCodeViewModel(private var qrText: String, title: String?) {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    companion object {
        private const val BITMAP_XY = 512
    }

    var contactQRCodeState: ContactQRCodeState by mutableStateOf(initialState())

    private fun initialState(): ContactQRCodeState = ContactQRCodeState(pubKey = qrText)

    private inline fun setContactState(update: ContactQRCodeState.() -> ContactQRCodeState) {
        contactQRCodeState = contactQRCodeState.update()
    }

    init {
        stringToQRCode()
    }

    private fun setQRCode(qrBitMatrix: BitMatrix) {
        setContactState {
            copy(
                bitMatrix = qrBitMatrix
            )
        }
    }

    private fun stringToQRCode(){
        scope.launch(dispatchers.default){
            val writer = QRCodeWriter()

            val qrText = if(qrText.isValidBech32()){
                qrText.uppercase()
            } else {
                qrText
            }

            val bitMatrix = writer.encode(
                qrText,
                BarcodeFormat.QR_CODE,
                BITMAP_XY,
                BITMAP_XY
            )

            setQRCode(bitMatrix)
        }
    }
}