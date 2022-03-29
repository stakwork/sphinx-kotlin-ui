package chat.sphinx.common.state

data class PINState(
    val sphinxPIN: String = "",
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
}