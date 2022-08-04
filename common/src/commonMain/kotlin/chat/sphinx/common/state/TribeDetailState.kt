package chat.sphinx.common.state

import chat.sphinx.wrapper.PhotoUrl

data class TribeDetailState(
    val tribeName: String = "",
    val tribePhotoUrl: PhotoUrl? = null,
    val createDate: String = "",
    val tribeConfigurations: String = "",
    val userAlias: String = "",
    val myPhotoUrl: PhotoUrl? = null,
    val tribeOwner: Boolean = false
    )
