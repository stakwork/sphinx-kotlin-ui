package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl

data class ProfileState(
   val alias: String = "",
   val nodePubKey: String = "",
   val routeHint: String = "",
   val serverUrl: String = "",
   val accountBalance: String = "0",
   val photoUrl: PhotoUrl? = null,
   val privatePhoto: Boolean? = null,
   val meetingServerUrl: String = "",
   val saveButtonEnabled: Boolean = false,
   val status: LoadResponse<Any, ResponseError>? = null,
   val profilePicture: MutableState<AttachmentInfo?> = mutableStateOf(null),
   val profilePictureResponse: LoadResponse<Any, ResponseError>? = null
)
