package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.*
import chat.sphinx.concepts.repository.connect_manager.model.NetworkStatus
import chat.sphinx.database.core.SphinxDatabaseQueries
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.repository.util.deleteAll
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.LightningNodeDescriptor
import chat.sphinx.wrapper.lightning.NodeBalance
import chat.sphinx.wrapper.lightning.VirtualLightningNodeAddress
import chat.sphinx.wrapper.tribe.TribeJoinLink
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

class DashboardViewModel(): WindowFocusListener {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val coreDB = SphinxContainer.appModule.coreDBImpl
    private val authenticationStorage = SphinxContainer.authenticationModule.authenticationStorage
    private val viewModelScope = SphinxContainer.appModule.applicationScope
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val connectManagerRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).connectManagerRepository

    enum class WebViewState {
        NonInitialized,
        Loading,
        Initialized,
        Error,
        RestartRequired
    }

    private val webViewState: MutableStateFlow<WebViewState> by lazy {
        MutableStateFlow(WebViewState.NonInitialized)
    }

    private val _balanceStateFlow: MutableStateFlow<NodeBalance?> by lazy {
        MutableStateFlow(null)
    }

    val balanceStateFlow: StateFlow<NodeBalance?>
        get() = _balanceStateFlow.asStateFlow()


    val accountOwnerStateFlow: StateFlow<Contact?>
        get() = contactRepository.accountOwner

    private val _packageVersionAndUpgrade: MutableStateFlow<Pair<String?, Boolean>> by lazy {
        MutableStateFlow(Pair(null, false))
    }

    val packageVersionAndUpgrade: StateFlow<Pair<String?, Boolean>>
        get() = _packageVersionAndUpgrade.asStateFlow()


    private val _contactWindowStateFlow: MutableStateFlow<Pair<Boolean, ContactScreenState?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val contactWindowStateFlow: StateFlow<Pair<Boolean, ContactScreenState?>>
        get() = _contactWindowStateFlow.asStateFlow()


    fun setWebViewState(state: WebViewState) {
        webViewState.value = state
    }

    fun isWebViewLoading() : Boolean {
        return webViewState.value == WebViewState.Loading
    }

    fun isWebViewLoaded() : Boolean {
        return webViewState.value == WebViewState.Initialized
    }

    fun getWebViewState() : WebViewState {
        return webViewState.value
    }

    fun toggleContactWindow(open: Boolean, screen: ContactScreenState?) {
        _contactWindowStateFlow.value = Pair(open, screen)
    }

    private val _aboutSphinxStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val aboutSphinxStateFlow: StateFlow<Boolean>
        get() = _aboutSphinxStateFlow.asStateFlow()

    fun toggleAboutSphinxWindow(open: Boolean) {
        _aboutSphinxStateFlow.value = open
    }

    private val _tribeDetailWindowStateFlow: MutableStateFlow<Pair<Boolean, ChatId?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val tribeDetailStateFlow: StateFlow<Pair<Boolean, ChatId?>>
        get() = _tribeDetailWindowStateFlow.asStateFlow()

    fun toggleTribeDetailWindow(open: Boolean, chatId: ChatId?) {
        _tribeDetailWindowStateFlow.value = Pair(open, chatId)
    }

    private val _qrWindowStateFlow: MutableStateFlow<Pair<Boolean, Pair<String, String>?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val qrWindowStateFlow: StateFlow<Pair<Boolean, Pair<String, String>?>>
        get() = _qrWindowStateFlow.asStateFlow()

    fun toggleQRWindow(
        open: Boolean,
        title: String? = null,
        value: String? = null
    ) {
        title?.let { nnTitle ->
            value?.let { nnValue ->
                _qrWindowStateFlow.value = Pair(open, Pair(nnTitle, nnValue))
                return
            }
        }
        _qrWindowStateFlow.value = Pair(open, null)
    }

    private val _profileStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val profileStateFlow: StateFlow<Boolean>
        get() = _profileStateFlow.asStateFlow()

    fun toggleProfileWindow(open: Boolean) {
        _profileStateFlow.value = open
    }

    private val _transactionsStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val transactionsStateFlow: StateFlow<Boolean>
        get() = _transactionsStateFlow.asStateFlow()

    fun toggleTransactionsWindow(open: Boolean) {
        _transactionsStateFlow.value = open
    }

    private val _createTribeStateFlow: MutableStateFlow<Pair<Boolean, ChatId?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val createTribeStateFlow: StateFlow<Pair<Boolean, ChatId?>>
        get() = _createTribeStateFlow.asStateFlow()

    fun toggleCreateTribeWindow(open: Boolean, chatId: ChatId?) {
        _createTribeStateFlow.value = Pair(open, chatId)
    }

    private val _joinTribeStateFlow: MutableStateFlow<Pair<Boolean, TribeJoinLink?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val joinTribeStateFlow: StateFlow<Pair<Boolean, TribeJoinLink?>>
        get() = _joinTribeStateFlow.asStateFlow()

    fun toggleJoinTribeWindow(open: Boolean, tribeJoinLink: TribeJoinLink? = null) {
        _joinTribeStateFlow.value = Pair(open, tribeJoinLink)
    }

    private val _backUpWindowStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val backUpWindowStateFlow: StateFlow<Boolean>
        get() = _backUpWindowStateFlow.asStateFlow()

    fun toggleBackUpWindow(open: Boolean) {
        _backUpWindowStateFlow.value = open
    }

    private val _changePinWindowStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val changePinWindowStateFlow: StateFlow<Boolean>
        get() = _changePinWindowStateFlow.asStateFlow()

    fun toggleChangePinWindow(open: Boolean) {
        _changePinWindowStateFlow.value = open
    }

    val unseenTribeMessagesCount: StateFlow<Long?> = repositoryDashboard.getUnseenTribeMessagesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)

    private fun getNodeDescriptor(owner: Contact): LightningNodeDescriptor? {
        owner.routeHint?.let {
            if (it.value.isNotEmpty()) {
                return VirtualLightningNodeAddress("${owner.nodePubKey?.value ?: ""}_${it.value}")
            }
        }
        return owner.nodePubKey
    }

    fun triggerOwnerQRCode() {
        val owner = accountOwnerStateFlow.value
        val nodeDescriptor = owner?.let { getNodeDescriptor(it) }
        toggleQRWindow(true, "PUBLIC KEY", nodeDescriptor?.value ?: "")
    }

    fun forceDisconnectMqtt() {
        connectManagerRepository.disconnectMqtt()
    }

    private fun getPackageVersion(){
        val currentAppVersion = "1.0.28"

        viewModelScope.launch(dispatchers.mainImmediate) {
            // TODO V2 getAppVersions
//            networkQueryVersion.getAppVersions().collect { loadResponse ->
//                when (loadResponse) {
//                    is Response.Error -> {
//                        _packageVersionAndUpgrade.value = Pair(currentAppVersion, false)
//                    }
//                    is Response.Success -> {
//                        val serverHubVersion = loadResponse.value.kmm
//
//                        currentAppVersion.replace(".", "").toIntOrNull()?.let { currentVersion ->
//                            if (serverHubVersion > currentVersion) {
//                                _packageVersionAndUpgrade.value = Pair(currentAppVersion, true)
//                            }
//                            else {
//                                _packageVersionAndUpgrade.value = Pair(currentAppVersion, false)
//                            }
//                        }
//                    }
//                    is LoadResponse.Loading -> {
//
//                    }
//                }
//            }
        }
    }


    private var screenInit: Boolean = false
    fun screenInit() {
        if (screenInit) {
            return
        } else {
            screenInit = true
        }
        connectManagerRepository.connectAndSubscribeToMqtt()
        networkRefresh()
        getPackageVersion()
        // TODO V2 getAccountBalanceStateFlow

        viewModelScope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.getAccountBalanceStateFlow().collect {
                _balanceStateFlow.value = it
            }
        }
    }

    override fun windowGainedFocus(p0: WindowEvent?) {
        if (DashboardScreenState.screenState() == DashboardScreenType.Unlocked) {
            networkRefresh()
        }
    }


    override fun windowLostFocus(p0: WindowEvent?) { }

    private val _restoreStateFlow: MutableStateFlow<RestoreProgress?> by lazy {
        MutableStateFlow(null)
    }

    val networkStatusStateFlow: StateFlow<NetworkStatus>
        get() = connectManagerRepository.networkStatus.asStateFlow()

    val restoreProgressStateFlow: StateFlow<RestoreProgress?>
        get() = connectManagerRepository.restoreProgress.asStateFlow()


    var isRestoreCancelledState: Boolean by mutableStateOf(initialRestoreCancelledState())

    private fun initialRestoreCancelledState(): Boolean = false

    private var jobRestore: Job? = null


    fun networkRefresh() {
        jobRestore = viewModelScope.launch(dispatchers.mainImmediate) {
            restoreProgressStateFlow.collect { response ->
                response?.let { restoreProgress ->
                    if (restoreProgress.restoring) {
                        _restoreStateFlow.value = restoreProgress
                    }
                }
            }
//
//            repositoryDashboard.networkRefreshLatestContacts.collect { response ->
//                Exhaustive@
//                when (response) {
//                    is LoadResponse.Loading -> {
//                        _networkStateFlow.value = response
//                    }
//                    is Response.Error -> {
//                        _networkStateFlow.value = response
//                    }
//                    is Response.Success -> {
//                        val restoreProgress = response.value
//
//                        if (restoreProgress.restoring) {
//                            _restoreStateFlow.value = restoreProgress
//                        }
//                    }
//                }
//            }
//
//            if (_networkStateFlow.value is Response.Error) {
//                jobNetworkRefresh?.cancel()
//            }
//

//
//            if (_networkStateFlow.value is Response.Error) {
//                jobNetworkRefresh?.cancel()
//            }
        }
    }

    private var jobNetworkRefresh: Job? = null

    fun triggerNetworkRefresh() {
        if (jobNetworkRefresh?.isActive == true) {
            return
        }

        jobNetworkRefresh = viewModelScope.launch(dispatchers.mainImmediate) {
            connectManagerRepository.reconnectMqtt()
        }
    }


    fun cancelRestore() {
        jobRestore?.cancel()
        _restoreStateFlow.value = null
        isRestoreCancelledState = true
    }

    fun clearDatabase() {
        coreDB.getSphinxDatabaseQueriesOrNull()?.let { queries: SphinxDatabaseQueries ->
            queries.transaction {
                deleteAll(queries)
            }
        }
    }

}