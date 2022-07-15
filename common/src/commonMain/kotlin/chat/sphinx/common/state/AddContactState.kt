package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError

data class AddContactState(
    val contactAlias: String = "",
    val lightningNodePubKey: String = "",
    val lightningRouteHint: String? = null,

    val status: LoadResponse<Any, ResponseError>? = null,

    val saveButtonEnabled: Boolean = false,
)
