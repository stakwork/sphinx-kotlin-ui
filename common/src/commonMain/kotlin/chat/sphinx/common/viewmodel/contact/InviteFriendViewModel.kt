package chat.sphinx.common.viewmodel.contact

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.InviteFriendState
import chat.sphinx.common.state.JoinTribeState
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.network.query.invite.NetworkQueryInvite
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class InviteFriendViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val networkQueryInvite: NetworkQueryInvite = SphinxContainer.networkModule.networkQueryInvite
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository


    var inviteFriendState: InviteFriendState by mutableStateOf(initialState())

    private fun initialState(): InviteFriendState = InviteFriendState()

    private inline fun setInviteFriendState(update: InviteFriendState.() -> InviteFriendState) {
        inviteFriendState = inviteFriendState.update()
    }

    init {
        getNodePrice()
    }

    private fun getNodePrice() {

        setInviteFriendState {
            copy(
                nodePriceStatus = LoadResponse.Loading
            )
        }
        scope.launch(dispatchers.mainImmediate) {
            networkQueryInvite.getLowestNodePrice().collect { loadResponse ->
                when (loadResponse) {
                    is Response.Success -> {
                        loadResponse.value.response?.price?.let { price ->
                            setInviteFriendState {
                                copy(
                                    nodePrice = price.toString(),
                                    nodePriceStatus = loadResponse
                                )
                            }
                        }
                    }
                }

            }
        }
    }
    fun onNicknameChange(text: String){
        setInviteFriendState {
            copy(
                nickname = text
            )
        }
    }
    fun onWelcomeMessageChange(text: String){
        setInviteFriendState {
            copy(
                welcomeMessage = text
            )
        }
    }

}