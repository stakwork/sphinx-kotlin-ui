package chat.sphinx.common.viewmodel.dashboard

import chat.sphinx.common.viewmodel.PinAuthenticationViewModel
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.di.container.SphinxContainer
import io.ktor.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.cryptonode.jncryptor.AES256JNCryptor
import org.cryptonode.jncryptor.CryptorException

class PinExportKeysViewModel : PinAuthenticationViewModel() {

    private val _backUpKey: MutableStateFlow<String?> by lazy {
        MutableStateFlow(null)
    }
    val backUpKey: StateFlow<String?>
        get() = _backUpKey.asStateFlow()

    fun setBackUpKey(key: String?){
        _backUpKey.value = key
    }
    val copiedToClipBoard = MutableStateFlow<Boolean>(false)


    @OptIn(RawPasswordAccess::class, InternalAPI::class)
    override fun onAuthenticationSucceed() {
        scope.launch(dispatchers.mainImmediate) {
            authenticationCoreManager.getEncryptionKey()?.let { encryptionKey ->
                val relayDataHandler = SphinxContainer.networkModule.relayDataHandlerImpl

                val passwordPin = Password(pinState.sphinxPIN.toCharArray())
                val relayUrl = relayDataHandler.retrieveRelayUrl()?.value
                val authToken = relayDataHandler.retrieveAuthorizationToken()?.value
                val privateKey = String(encryptionKey.privateKey.value)
                val publicKey = String(encryptionKey.publicKey.value)

                val keysString = "$privateKey::$publicKey::${relayUrl}::${authToken}"

                try {
                    val encryptedString = AES256JNCryptor()
                        .encryptData(keysString.toByteArray(), passwordPin.value)
                        .encodeBase64()

                    val finalString = "keys::${encryptedString}"
                        .toByteArray()
                        .encodeBase64()

                    setBackUpKey(finalString)

                } catch (e: CryptorException) {
//                    submitSideEffect(ProfileSideEffect.BackupKeysFailed)
                } catch (e: IllegalArgumentException) {
//                    submitSideEffect(ProfileSideEffect.BackupKeysFailed)
                }
            }
        }
    }

}
