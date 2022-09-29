package chat.sphinx.common.viewmodel.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.Res
import chat.sphinx.common.state.CreateTribeState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.repository.chat.model.CreateTribe
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.feed.toFeedTypeString
import chat.sphinx.wrapper.feed.toFeedType
import chat.sphinx.wrapper.toPhotoUrl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.Path
import theme.badge_red

class CreateTribeViewModel(
    private val dashboardViewModel: DashboardViewModel,
    private val chatId: ChatId?
) {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val repositoryModule = SphinxContainer.repositoryModule(sphinxNotificationManager)
    private val networkModule = SphinxContainer.networkModule
    private val networkQueryChat: NetworkQueryChat = networkModule.networkQueryChat
    private val chatRepository = repositoryModule.chatRepository
    private val mediaCacheHandler = SphinxContainer.appModule.mediaCacheHandler
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository


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

    private val accountOwnerStateFlow: StateFlow<Contact?>
        get() = contactRepository.accountOwner

    private val createTribeBuilder = CreateTribe.Builder(
        tribeTagListState.value
    )

    fun getTagNameList() {
        val list = arrayListOf<String>()
        _tribeTagListStateFlow.value.forEach { tag ->
            if (tag.isSelected) {
                list.add(tag.name)
            }
            setCreateTribeState {
                copy(
                    tags = list
                )
            }
            checkValidInput()
        }
    }

    private var loadJob: Job? = null
    fun loadTribe() {
        if (loadJob?.isActive == true) {
            return
        }

        loadJob = scope.launch(dispatchers.mainImmediate) {
            accountOwnerStateFlow.collect { contactOwner ->
                contactOwner?.let { owner ->
                    chatId?.let { chatId ->
                        chatRepository.getChatById(chatId)?.let { chat ->
                            val host = chat.host

                            if (host != null) {
                                networkQueryChat.getTribeInfo(host, chat.uuid).collect { loadResponse ->
                                    when (loadResponse) {
                                        is LoadResponse.Loading -> {}

                                        is Response.Error -> {
                                            toast("There was an error, please try later")
                                        }

                                        is Response.Success -> {
                                            createTribeBuilder.load(loadResponse.value)
                                            getTagNameList()
                                            setCreateTribeState {
                                                copy(
                                                    name = loadResponse.value.name,
                                                    img = loadResponse.value.img?.toPhotoUrl(),
                                                    imgUrl = loadResponse.value.img ?: "",
                                                    description = loadResponse.value.description,
                                                    priceToJoin = loadResponse.value.price_to_join,
                                                    pricePerMessage = loadResponse.value.price_per_message,
                                                    escrowAmount = loadResponse.value.escrow_amount,
                                                    escrowMillis = loadResponse.value.escrow_millis,
                                                    appUrl = loadResponse.value.app_url ?: "",
                                                    feedUrl = loadResponse.value.feed_url ?: "",
                                                    feedType = loadResponse.value.feed_type?.toFeedType()
                                                        ?.toFeedTypeString() ?: "",
                                                    private = loadResponse.value.private.value,
                                                    unlisted = loadResponse.value.unlisted.value
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    init {
        loadTribe()
    }

    fun changeSelectTag(position: Int) {
        _tribeTagListStateFlow.value[position].isSelected = !_tribeTagListStateFlow.value[position].isSelected
    }

    private var saveTribeJob: Job? = null
    fun saveTribe() {
        if (saveTribeJob?.isActive == true) {
            return
        }
        setTribeBuilder()

        setCreateTribeState {
            copy(
                saveTribeResponse = LoadResponse.Loading
            )
        }

        if (createTribeBuilder.hasRequiredFields) {
            createTribeBuilder.build()?.let {
                saveTribeJob = scope.launch(dispatchers.mainImmediate) {
                    if (chatId == null) {
                        when (chatRepository.createTribe(it)) {
                            is Response.Error -> {
                                setCreateTribeState {
                                    toast("There was an error, please try later")
                                    copy(
                                        saveTribeResponse = null
                                    )
                                }
                            }
                            is Response.Success -> {
                                dashboardViewModel.toggleCreateTribeWindow(false, null)
                            }
                        }
                    } else {
                        when (chatRepository.updateTribe(chatId, it)) {
                            is Response.Error -> {
                                toast("There was an error, please try later")
                                setCreateTribeState {
                                    copy(
                                        saveTribeResponse = null
                                    )
                                }
                            }
                            is Response.Success -> {
                                dashboardViewModel.toggleCreateTribeWindow(false, null)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setTribeBuilder() {
        createTribeBuilder.setName(createTribeState.name)
        createTribeBuilder.setDescription(createTribeState.description)
        createTribeBuilder.setImg(createTribeState.path)
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
            copy(path = filepath)
        }
        checkValidInput()
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
        checkValidInput()
    }


    fun onPricePerMessageChanged(text: String) {
        setCreateTribeState {
            copy(pricePerMessage = getLong(text))
        }
        checkValidInput()
    }

    fun onAmountToStakeChanged(text: String) {
        setCreateTribeState {
            copy(escrowAmount = getLong(text))
        }
        checkValidInput()
    }

    fun onTimeToStakeChanged(text: String) {
        val hours = (getLong(text) * 60 * 60 * 1000)
        setCreateTribeState {
            copy(escrowMillis = hours)
        }
        checkValidInput()
    }

    fun onAppUrlChanged(text: String) {
        setCreateTribeState {
            copy(appUrl = text)
        }
        checkValidInput()
    }

    fun onFeedUrlChanged(text: String) {
        setCreateTribeState {
            copy(feedUrl = text)
        }
        checkValidInput()
    }

    fun onFeedContentTypeChanged(text: String) {
        setCreateTribeState {
            copy(feedType = text)
        }
        checkValidInput()
    }

    fun onUnlistedChanged(unlisted: Boolean) {
        setCreateTribeState {
            copy(unlisted = unlisted)
        }
        checkValidInput()
    }

    fun onPrivateChanged(private: Boolean) {
        setCreateTribeState {
            copy(private = private)
        }
        checkValidInput()
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

    fun toast(
        message: String,
        color: Color = badge_red,
        delay: Long = 2000L
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.toast(
                "Sphinx",
                message,
                color.value,
                delay
            )
        }
    }

}