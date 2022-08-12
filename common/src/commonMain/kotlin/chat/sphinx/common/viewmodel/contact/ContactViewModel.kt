package chat.sphinx.common.viewmodel.contact

import chat.sphinx.common.state.ContactState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.wrapper.lightning.LightningNodeDescriptor
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.VirtualLightningNodeAddress
import chat.sphinx.wrapper.lightning.toLightningNodePubKey
import kotlinx.coroutines.Job

abstract class ContactViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    protected var saveContactJob: Job? = null
    abstract var contactState: ContactState

    abstract fun saveContact()

    abstract fun onNicknameTextChanged(text: String)
    abstract fun onAddressTextChanged(text: String)
    abstract fun onRouteHintTextChanged(text: String)

    fun getNodeDescriptor(): LightningNodeDescriptor? {
        contactState.lightningRouteHint?.let {
            if (it.isNotEmpty()) {
                return VirtualLightningNodeAddress("${contactState.lightningNodePubKey}:${it}")
            }
        }
        return contactState.lightningNodePubKey.toLightningNodePubKey()
    }

}