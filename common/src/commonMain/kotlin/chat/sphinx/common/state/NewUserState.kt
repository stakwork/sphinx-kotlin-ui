package chat.sphinx.common.state

data class NewUserState(
    val invitationCodeText: String = "",
    val errorMessage: String? = null,
    val isProcessing: Boolean? = null
) {
}