package chat.sphinx.common.viewmodel.contact

import chat.sphinx.common.state.ContactState
import chat.sphinx.di.container.SphinxContainer
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

}