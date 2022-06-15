package chat.sphinx.common.state

data class RestoreExistingUserState(
    val sphinxKeys: String = "",
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val success: String? = null,
    val isLoading: Boolean? = false,
) {
}