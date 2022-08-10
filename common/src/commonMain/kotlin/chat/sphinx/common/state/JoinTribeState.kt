package chat.sphinx.common.state

import chat.sphinx.concepts.repository.message.model.AttachmentInfo
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.serialization.SphinxBoolean
import chat.sphinx.wrapper.PhotoUrl

data class JoinTribeState(
    val name: String = "",
    val description: String = "",
    val img: PhotoUrl? = null,
    val price_to_join: String = "",
    val price_per_message: String = "",
    val escrow_amount: String = "",
    val hourToStake: String = "",
    val userAlias: String = "",
    val myPhotoUrl: PhotoUrl? = null,
    var status: LoadResponse<Any, ResponseError>? = null,
    val userPicture: AttachmentInfo? = null,
    val photoUrlText: String = ""
    )
