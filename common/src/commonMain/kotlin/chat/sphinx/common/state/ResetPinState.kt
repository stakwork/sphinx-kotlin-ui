package chat.sphinx.common.state

import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse

data class ResetPinState(
    val currentPin: String = "",
    val newPin: String = "",
    val confirmedPin: String = "",

    val buttonEnabled: Boolean = false,
    val errorMessage: String? = null,
    val status: AuthenticateFlowResponse? = null,
)
