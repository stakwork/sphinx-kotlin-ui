package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.AppState
import chat.sphinx.common.state.ScreenType
import chat.sphinx.common.state.SphinxState
import chat.sphinx.di.container.SphinxContainer
import kotlinx.coroutines.launch

class SphinxStore {
    private val scope = SphinxContainer.appModule.applicationScope
    private val authenticationManager = SphinxContainer.authenticationModule.authenticationCoreManager
    private val authenticationStorage = SphinxContainer.authenticationModule.authenticationStorage
    private val encryptionKeyHandler = SphinxContainer.authenticationModule.encryptionKeyHandler

    var state: SphinxState by mutableStateOf(initialState())
        private set

    private fun initialState(): SphinxState =
        SphinxState()

    fun removeAccount() {
        // TODO: logout Confirmation...
        scope.launch(SphinxContainer.appModule.dispatchers.main) {
            // TODO: Close all pending activities...
            SphinxContainer.appModule.sphinxCoreDBImpl.deleteDatabase()
            authenticationStorage.clearAuthenticationStorage()
            authenticationManager.logOut()
            encryptionKeyHandler.clearKeysToRestore()
            // TODO: Restart DB...
            AppState.screenState(ScreenType.LandingScreen)
        }
    }

    private inline fun setState(update: SphinxState.() -> SphinxState) {
        state = state.update()
    }
}