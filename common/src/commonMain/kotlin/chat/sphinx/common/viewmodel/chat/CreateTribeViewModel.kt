package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.CreateTribeState
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import com.squareup.sqldelight.internal.copyOnWriteList
import okio.Path

class CreateTribeViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val repositoryModule = SphinxContainer.repositoryModule(sphinxNotificationManager)
    private val networkModule = SphinxContainer.networkModule
    private val networkQueryChat: NetworkQueryChat = networkModule.networkQueryChat
    private val chatRepository = repositoryModule.chatRepository
    private val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler


    var createTribeState: CreateTribeState by mutableStateOf(initialCreateTribeState())

    private fun initialCreateTribeState(): CreateTribeState = CreateTribeState()

    private inline fun setCreateTribeState(update: CreateTribeState.() -> CreateTribeState) {
        createTribeState = createTribeState.update()
    }

    fun onNameChanged(text: String) {
        setCreateTribeState {
            copy(name = text)
        }
    }

    fun onPictureChanged(filepath: Path) {
        setCreateTribeState {
            copy(img = filepath)
        }
    }

    fun onDescriptionChanged(text: String) {
        setCreateTribeState {
            copy(description = text)
        }
    }

    fun onPriceToJoinChanged(text: String) {
        setCreateTribeState {
            copy(priceToJoin = getLong(text))
        }
    }


    fun onPricePerMessageChanged(text: String) {
        setCreateTribeState {
            copy(pricePerMessage = getLong(text))
        }
    }

    fun onAmountToStakeChanged(text: String) {
        setCreateTribeState {
            copy(escrowAmount = getLong(text))
        }
    }
    fun onTimeToStakeChanged(text: String) {
        setCreateTribeState {
            copy(escrowMillis = getLong(text))
        }
    }

    fun onAppUrlChanged(text: String){
        setCreateTribeState {
            copy(appUrl = text)
        }
    }

    fun onFeedUrlChanged(text: String){
        setCreateTribeState {
            copy(feedUrl = text)
        }
    }

    fun onUnlistedChanged(unlisted: Boolean){
        setCreateTribeState {
            copy(unlisted = unlisted)
        }
    }

    fun onPrivateChanged(private: Boolean){
        setCreateTribeState {
            copy(private = private)
        }
    }

    private fun getLong(text: String): Long {
        val amount = try {
            text.toLong()
        } catch (e: NumberFormatException) {
            0
        }
        return amount
    }


}