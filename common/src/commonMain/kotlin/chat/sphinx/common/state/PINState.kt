package chat.sphinx.common.state

data class PINState(
    val sphinxPIN: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
) {
}