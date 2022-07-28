package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse

data class ResetPinState(
    val currentPin: String = "",
    val newPin: String = "",
    val confirmedPin: String = "",
    val status: AuthenticateFlowResponse? = null,
    val confirmButtonState: Boolean = false
)
