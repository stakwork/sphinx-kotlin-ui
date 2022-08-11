package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError

data class InviteFriendState(
    val nickname: String = "",
    val welcomeMessage: String = "",
    val nodePrice: String = "0",
    var nodePriceStatus: LoadResponse<Any, ResponseError>? = null,
    var createInviteStatus: LoadResponse<Any, ResponseError>? = null,
    )
