package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.state.InviteFriendState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.lightning.NodeBalance
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import theme.primary_red

class InviteFriendViewModel(
    val dashboardViewModel: DashboardViewModel
) {
    val scope = SphinxContainer.appModule.applicationScope
    private val viewModelScope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val connectManagerRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).connectManagerRepository
    private val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard

    var inviteFriendState: InviteFriendState by mutableStateOf(initialState())

    private fun initialState(): InviteFriendState = InviteFriendState()

    private inline fun setInviteFriendState(update: InviteFriendState.() -> InviteFriendState) {
        inviteFriendState = inviteFriendState.update()
    }

    private val _balanceStateFlow: MutableStateFlow<NodeBalance?> by lazy {
        MutableStateFlow(null)
    }

    val balanceStateFlow: StateFlow<NodeBalance?>
        get() = _balanceStateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.getAccountBalanceStateFlow().collect {
                _balanceStateFlow.value = it
            }
        }
    }

    private var createInviteJob: Job? = null
    fun createNewInvite(){
        if (createInviteJob?.isActive == true) {
            return
        }
        createInviteJob = scope.launch(dispatchers.mainImmediate) {

            val balance = balanceStateFlow.value?.balance?.value
            val nickname = inviteFriendState.nickname
            val message = if (inviteFriendState.welcomeMessage.trim().isNotEmpty()) {
                inviteFriendState.welcomeMessage
            } else {
                "Welcome to Sphinx!"
            }
            val amount = inviteFriendState.amount.toLongOrNull()

            if (amount == null || amount <= 0) {
                toast("Please enter a valid amount")
                return@launch
            }

            if (balance == null || amount >= balance) {
                toast("Insufficient balance")
                return@launch
            }

            connectManagerRepository.createInvite(
                nickname = nickname,
                welcomeMessage = message,
                sats = amount
            )

            dashboardViewModel.toggleContactWindow(false, null)
        }
    }

    fun onNicknameChange(text: String) {
        setInviteFriendState {
            copy(
                nickname = text
            )
        }
    }

    fun onAmountChange(text: String) {
        setInviteFriendState {
            copy(
                amount = text
            )
        }
    }

    fun onWelcomeMessageChange(text: String) {
        setInviteFriendState {
            copy(
                welcomeMessage = text
            )
        }
    }

    private fun toast(
        message: String,
        color: Color = primary_red,
        delay: Long = 2000L
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.toast(
                "Add New Friend",
                message,
                color.value,
                delay
            )
        }
    }

}