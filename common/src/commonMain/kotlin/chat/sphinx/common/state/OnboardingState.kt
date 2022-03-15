package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


enum class OnboardingStatus {
    Connecting,
    Failed,
    Successful
}

object OnboardingState {
    private var status: MutableState<OnboardingStatus?> = mutableStateOf(null)

    fun status() : OnboardingStatus? {
        return status.value
    }

    fun status(state: OnboardingStatus?) {
        status.value = state
    }

}