package chat.sphinx.common.viewmodel.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.BackupKeysState
import chat.sphinx.common.viewmodel.PinAuthenticationViewModel
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.di.container.SphinxContainer
import io.ktor.util.*
import kotlinx.coroutines.launch
import org.cryptonode.jncryptor.AES256JNCryptor
import org.cryptonode.jncryptor.CryptorException
import kotlin.text.toCharArray

class PinExportKeysViewModel : PinAuthenticationViewModel() {

    var backupKeysState: BackupKeysState by mutableStateOf(initialState())

    val relayDataHandler = SphinxContainer.networkModule.relayDataHandlerImpl

    private fun initialState(): BackupKeysState = BackupKeysState()

    private inline fun setBackupKeysState(update: BackupKeysState.() -> BackupKeysState) {
        backupKeysState = backupKeysState.update()
    }

    override fun onPINTextChanged(text: String) {
        super.onPINTextChanged(text)
        backupKeysState = initialState()
    }

    @OptIn(RawPasswordAccess::class, InternalAPI::class)
    override fun onAuthenticationSucceed() {
        scope.launch(dispatchers.mainImmediate) {
            authenticationCoreManager.getEncryptionKey()?.let { encryptionKey ->
                val passwordPin = Password(pinState.sphinxPIN.toCharArray())
                val authToken = relayDataHandler.retrieveAuthorizationToken()?.value
                val privateKey = String(encryptionKey.privateKey.value)
                val publicKey = String(encryptionKey.publicKey.value)

                val keysString = "$privateKey::$publicKey::${authToken}"

                try {
                    val encryptedString = AES256JNCryptor()
                        .encryptData(keysString.toByteArray(), passwordPin.value)
                        .encodeBase64()

                    val finalString = "keys::${encryptedString}"
                        .toByteArray()
                        .encodeBase64()

                    setBackupKeysState {
                        copy(
                            restoreString = finalString,
                            error = false
                        )
                    }

                } catch (e: CryptorException) {
                    setBackupKeysState {
                        copy(
                            restoreString = null,
                            error = true
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    setBackupKeysState {
                        copy(
                            restoreString = null,
                            error = true
                        )
                    }
                }
            }
        }
    }

}
