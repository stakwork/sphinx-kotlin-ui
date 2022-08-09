package chat.sphinx.common.state

import chat.sphinx.serialization.SphinxBoolean
import chat.sphinx.wrapper.PhotoUrl

data class JoinTribeState(
    val name: String = "",
    val description: String = "",
    val img: PhotoUrl? = null,
    val tags: Array<String> = arrayOf(),
    val group_key: String = "",
    val owner_pubkey: String = "",
    val owner_route_hint: String? = "",
    val owner_alias: String? = "",
    val price_to_join: String = "",
    val price_per_message: String = "",
    val escrow_amount: String = "",
    val escrow_millis: String = "",
    val unlisted: SphinxBoolean? = null,
    val private: Any? = null,
    val deleted: Any? = null,
    val app_url: String? = "",
    val feed_url: String? = "",
    val feed_type: Int? = null,
    val userAlias: String = "",
    val userPhotoUrl: PhotoUrl? = null
)
