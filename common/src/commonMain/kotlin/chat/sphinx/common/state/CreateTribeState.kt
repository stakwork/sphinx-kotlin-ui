package chat.sphinx.common.state

import chat.sphinx.wrapper.chat.AppUrl
import chat.sphinx.wrapper.feed.FeedType
import chat.sphinx.wrapper.feed.FeedUrl
import okio.Path

data class CreateTribeState(
    val name: String = "",
    val imgUrl: String = "",
    val img: Path? = null,
    val description: String = "",
    val tags: Array<String>? = null,
    val priceToJoin: Long? = null,
    val pricePerMessage: Long? = null,
    val escrowAmount: Long? = null,
    val escrowMillis: Long? = null,
    val appUrl: String = "",
    val feedUrl: String = "",
    val feedType: String? = "",
    val unlisted: Boolean = true,
    val private: Boolean = false,
)
