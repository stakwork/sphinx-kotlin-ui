package chat.sphinx.common.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.*

class RestoreFromKeystoreStore {
    var state: RestoreFromKeystoreState by mutableStateOf(initialState())
        private set

    // TODO: Add logic to load from keystore...

    private fun initialState(): RestoreFromKeystoreState = RestoreFromKeystoreState()

    private inline fun setState(update: RestoreFromKeystoreState.() -> RestoreFromKeystoreState) {
        state = state.update()
    }
}