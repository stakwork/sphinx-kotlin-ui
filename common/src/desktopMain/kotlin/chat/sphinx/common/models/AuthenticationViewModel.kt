package chat.sphinx.common.models

import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.authentication.core.AuthenticationCoreManager

class AuthenticationViewModel {
    val authenticationManager: AuthenticationCoreManager = SphinxContainer.authenticationModule.authenticationCoreManager
    val dispatchers = SphinxContainer.appModule.dispatchers
}