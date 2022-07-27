package chat.sphinx.common.viewmodel.dashboard

import chat.sphinx.common.state.BackUpPinState
import chat.sphinx.common.state.PinType
import chat.sphinx.common.viewmodel.PinAuthenticationViewModel
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.di.container.SphinxContainer
import io.ktor.util.*
import kotlinx.coroutines.launch
import org.cryptonode.jncryptor.AES256JNCryptor
import org.cryptonode.jncryptor.CryptorException

class PinExportKeysViewModel : PinAuthenticationViewModel() {

    val dispatchers = SphinxContainer.appModule.dispatchers

    @OptIn(RawPasswordAccess::class, InternalAPI::class)
    override fun onAuthenticationSucceed() {
        BackUpPinState.pinState(PinType.Success)

        scope.launch(dispatchers.mainImmediate) {
            authenticationCoreManager.getEncryptionKey()?.let { encryptionKey ->
                val relayDataHandler = SphinxContainer.networkModule.relayDataHandlerImpl

                val pin = pinState.sphinxPIN.toCharArray()
                val relayUrl = relayDataHandler.retrieveRelayUrl()?.value
                val authToken = relayDataHandler.retrieveAuthorizationToken()?.value
                val privateKey = String(encryptionKey.privateKey.value)
                val publicKey = String(encryptionKey.publicKey.value)

                val keysString = "$privateKey::$publicKey::${relayUrl}::${authToken}"

                try {
                    val encryptedString = AES256JNCryptor()
                        .encryptData(keysString.toByteArray(), pin)
                        .encodeBase64()

                    val finalString = "keys::${encryptedString}"
                        .toByteArray()
                        .encodeBase64()

                    //TODO copy to clipboard

//                    submitSideEffect(ProfileSideEffect.CopyBackupToClipboard(finalString))
                } catch (e: CryptorException) {
//                    submitSideEffect(ProfileSideEffect.BackupKeysFailed)
                } catch (e: IllegalArgumentException) {
//                    submitSideEffect(ProfileSideEffect.BackupKeysFailed)
                }
            }
        }
    }

}