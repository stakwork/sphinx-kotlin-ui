package chat.sphinx.common.state

data class ExistingUserState(
    val sphinxKeys: String = "",
    val sphinxPIN: String = "",
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
}