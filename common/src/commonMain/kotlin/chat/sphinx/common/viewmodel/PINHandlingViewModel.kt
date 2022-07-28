package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.PINState
import chat.sphinx.di.container.SphinxContainer

abstract class PINHandlingViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val authenticationCoreManager = SphinxContainer.authenticationModule.authenticationCoreManager
    val dispatchers = SphinxContainer.appModule.dispatchers


    var pinState: PINState by mutableStateOf(initialState())
        protected set

    abstract fun onPINTextChanged(text: String)
    abstract fun onSubmitPIN()

    private fun initialState(): PINState = PINState()

    protected inline fun setPINState(update: PINState.() -> PINState) {
        pinState = pinState.update()
    }
}