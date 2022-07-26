package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl

data class ProfileState(
   val alias: String = "",
   val nodePubKey: String = "",
   val routeHint: String = "",
   val accountBalance: String = "0",
   val photoUrl: PhotoUrl? = null,
   val privatePhoto: Boolean? = null,
   val meetingServerUrl: String = "",
   val saveButtonEnabled: Boolean = false,
   val status: LoadResponse<Any, ResponseError>? = null,
   )
