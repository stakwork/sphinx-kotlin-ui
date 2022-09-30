package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl
import okio.Path

data class CreateTribeState(
    val name: String = "",
    val imgUrl: String = "",
    val path: Path? = null,
    val img: PhotoUrl? = null,
    val description: String = "",
    val tags: ArrayList<String> = arrayListOf(""),
    val priceToJoin: Long? = null,
    val pricePerMessage: Long? = null,
    val escrowAmount: Long? = null,
    val escrowHours: Long? = null,
    val appUrl: String = "",
    val feedUrl: String = "",
    val feedType: String = "",
    val unlisted: Boolean = false,
    val private: Boolean = false,
    val buttonEnabled: Boolean = false,
    val saveTribeResponse: LoadResponse<Any, ResponseError>? = null
)
