package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.Res
import chat.sphinx.common.state.CreateTribeState
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.repository.chat.model.CreateTribe
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    private val _tribeTagListStateFlow: MutableStateFlow<Array<CreateTribe.Builder.Tag>> by lazy {
        MutableStateFlow(
            arrayOf(
                CreateTribe.Builder.Tag("Bitcoin", Res.drawable.ic_bitcoin),
                CreateTribe.Builder.Tag("Lightning", Res.drawable.ic_lightning),
                CreateTribe.Builder.Tag("Sphinx", Res.drawable.ic_sphinx),
                CreateTribe.Builder.Tag("Crypto", Res.drawable.ic_crypto),
                CreateTribe.Builder.Tag("Tech", Res.drawable.ic_tech),
                CreateTribe.Builder.Tag("Altcoins", Res.drawable.ic_altcoins),
                CreateTribe.Builder.Tag("Music", Res.drawable.ic_music),
                CreateTribe.Builder.Tag("Podcast", Res.drawable.ic_podcast)
            )
        )
    }

    val tribeTagListState: StateFlow<Array<CreateTribe.Builder.Tag>>
        get() = _tribeTagListStateFlow.asStateFlow()

    private val _tribeTagNameList: MutableStateFlow<List<String>> by lazy {
        MutableStateFlow(listOf(""))
    }

    val tribeTagNameList: StateFlow<List<String>>
        get() = _tribeTagNameList.asStateFlow()

    fun getTagNameList() {
        val list = arrayListOf<String>()
        _tribeTagListStateFlow.value.forEach { tag ->
            if (tag.isSelected) {
                list.add(tag.name)
            }
            _tribeTagNameList.value = list
        }
    }

    fun getTagSelected(tags: Array<String>) {
        tags.forEach { name ->
            _tribeTagListStateFlow.value.forEach { tag ->
                if (tag.name == name) {
                    tag.isSelected = true
                }
            }
        }
    }

    fun changeSelectTag(position: Int) {
        _tribeTagListStateFlow.value[position].isSelected = !_tribeTagListStateFlow.value[position].isSelected
    }

    val createTribeBuilder = CreateTribe.Builder(
        tribeTagListState.value
    )

    private var saveTribeJob: Job? = null
    fun saveTribe() {
        if (saveTribeJob?.isActive == true) {
            return
        }
        setTribeBuilder()

        if (createTribeBuilder.hasRequiredFields) {
            createTribeBuilder.build()?.let {
                saveTribeJob = scope.launch(dispatchers.mainImmediate) {
                    when (chatRepository.createTribe(it)) {
                        is Response.Error -> {}
                        is Response.Success -> {}
                    }
                }
            }
        }

    }

    private fun setTribeBuilder() {
        createTribeBuilder.setName(createTribeState.name)
        createTribeBuilder.setDescription(createTribeState.description)
        createTribeBuilder.setImg(createTribeState.img)
        createTribeBuilder.setPriceToJoin(createTribeState.priceToJoin)
        createTribeBuilder.setPricePerMessage(createTribeState.pricePerMessage)
        createTribeBuilder.setEscrowAmount(createTribeState.escrowAmount)
        createTribeBuilder.setEscrowMillis(createTribeState.escrowMillis)
        createTribeBuilder.setAppUrl(createTribeState.appUrl)
        createTribeBuilder.setFeedUrl(createTribeState.feedUrl)
        createTribeBuilder.setUnlisted(createTribeState.unlisted)
        createTribeBuilder.setPrivate(createTribeState.private)
    }

    fun onNameChanged(text: String) {
        setCreateTribeState {
            copy(name = text)
        }
        checkValidInput()
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
        checkValidInput()
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

    fun onAppUrlChanged(text: String) {
        setCreateTribeState {
            copy(appUrl = text)
        }
    }

    fun onFeedUrlChanged(text: String) {
        setCreateTribeState {
            copy(feedUrl = text)
        }
    }

    fun onUnlistedChanged(unlisted: Boolean) {
        setCreateTribeState {
            copy(unlisted = unlisted)
        }
    }

    fun onPrivateChanged(private: Boolean) {
        setCreateTribeState {
            copy(private = private)
        }
    }

    fun checkValidInput() {
        if (createTribeState.name.isNotEmpty() && createTribeState.description.isNotEmpty()) {
            setCreateTribeState {
                copy(buttonEnabled = true)
            }
        } else {
            setCreateTribeState {
                copy(buttonEnabled = false)
            }
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