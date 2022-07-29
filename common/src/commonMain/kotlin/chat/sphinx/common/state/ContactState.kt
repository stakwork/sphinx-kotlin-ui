package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl

data class ContactState(
    val contactAlias: String = "",
    val lightningNodePubKey: String = "",
    val lightningRouteHint: String? = null,
    val photoUrl: PhotoUrl? = null,
    val status: LoadResponse<Any, ResponseError>? = null,
    val saveButtonEnabled: Boolean = false,
)
