package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ContactState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import kotlinx.coroutines.Job

abstract class ContactViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    abstract var contactState: ContactState

    abstract fun onNicknameTextChanged(text: String)
    abstract fun onAddressTextChanged(text: String)
    abstract fun onRouteHintTextChanged(text: String)

    abstract fun saveContact()


}