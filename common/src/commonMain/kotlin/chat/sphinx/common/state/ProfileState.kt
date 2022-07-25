package chat.sphinx.common.state

import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.contact.PrivatePhoto
import chat.sphinx.wrapper.lightning.LightningNodePubKey
import chat.sphinx.wrapper.lightning.Sat

data class ProfileState(
   val alias: String = "",
   val nodePubKey: String = "",
   val routeHint: String = "",
   val accountBalance: String = "0",
   val photoUrl: PhotoUrl? = null,
   val privatePhoto: Boolean? = null,
   val defaultCallServer: String = ""

)
