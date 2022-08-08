package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl

data class TribeDetailState(
    val tribeName: String = "",
    val tribePhotoUrl: PhotoUrl? = null,
    val createDate: String = "",
    val tribeConfigurations: String = "",
    val userAlias: String = "",
    val userPicture: AttachmentInfo? = null,
    val myPhotoUrl: PhotoUrl? = null,
    val tribeOwner: Boolean = false,
    val shareTribeUrl: String = "",
    val saveButtonEnable: Boolean = false,
    val updateResponse: LoadResponse<Any, ResponseError>? = null
)
