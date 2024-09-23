package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.state.InviteFriendState
import chat.sphinx.common.state.JoinTribeState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.lightning.toSat
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import theme.primary_green
import theme.primary_red

class InviteFriendViewModel(
    val dashboardViewModel: DashboardViewModel
) {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository


    var inviteFriendState: InviteFriendState by mutableStateOf(initialState())

    private fun initialState(): InviteFriendState = InviteFriendState()

    private inline fun setInviteFriendState(update: InviteFriendState.() -> InviteFriendState) {
        inviteFriendState = inviteFriendState.update()
    }


    private var createInviteJob: Job? = null
    fun createNewInvite(){
        if (createInviteJob?.isActive == true) {
            return
        }
        createInviteJob = scope.launch(dispatchers.mainImmediate) {

            setInviteFriendState {
                copy(
                    createInviteStatus = LoadResponse.Loading
                )
            }

            val nickname = inviteFriendState.nickname
            val message = if (inviteFriendState.welcomeMessage.trim().isNotEmpty()) {
                inviteFriendState.welcomeMessage
            } else {
                "Welcome to Sphinx!"
            }

            contactRepository.createNewInvite(nickname, message).collect { loadResponse ->

                setInviteFriendState {
                    copy(
                        createInviteStatus = loadResponse
                    )
                }

                when(loadResponse){
                    is LoadResponse.Loading -> {}
                    is Response.Error -> {
                        toast("There was an error, please try later")
                    }
                    is Response.Success -> {
                        dashboardViewModel.toggleContactWindow(false, null)
                    }
                }
            }
        }

    }

    fun onNicknameChange(text: String) {
        setInviteFriendState {
            copy(
                nickname = text
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
                "Contact UI",
                message,
                color.value,
                delay
            )
        }
    }

}