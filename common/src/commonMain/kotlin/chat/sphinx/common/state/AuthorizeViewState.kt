package chat.sphinx.common.state

import chat.sphinx.wrapper.contact.Contact

sealed class AuthorizeViewState {

    abstract val url: String?

    data class Closed(
        override val url: String? = null
    ) : AuthorizeViewState()

    data class Opened(
        override val url: String,
        val budgetField: Boolean = false
    ) : AuthorizeViewState()
}