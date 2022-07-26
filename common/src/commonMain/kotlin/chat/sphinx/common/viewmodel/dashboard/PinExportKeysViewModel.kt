package chat.sphinx.common.viewmodel.dashboard

import chat.sphinx.common.state.BackUpPinState
import chat.sphinx.common.state.PinType
import chat.sphinx.common.viewmodel.PinAuthenticationViewModel

class PinExportKeysViewModel : PinAuthenticationViewModel() {

    override fun onAuthenticationSucceed() {
        BackUpPinState.pinState(PinType.Success)

        val pin = pinState.sphinxPIN
    }

}