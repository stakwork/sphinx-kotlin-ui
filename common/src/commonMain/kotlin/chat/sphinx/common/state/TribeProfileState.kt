package chat.sphinx.common.state

data class TribeProfileState(
    val name: String = "",
    val description: String = "",
    val profilePicture: String = "empty",
    val codingLanguages: String = "",
    val priceToMeet: String = "0",
    val posts: String = "",
    val twitter: String = "",
    val github: String = "",
    val loadingTribeProfile: Boolean = true,
    )
