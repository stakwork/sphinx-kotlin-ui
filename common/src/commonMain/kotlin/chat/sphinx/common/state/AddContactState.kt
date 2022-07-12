package chat.sphinx.common.state

data class AddContactState(
    val contactAlias: String = "",
    val lightningNodePubKey: String = "",
    val lightningRouteHint: String? = null
)
