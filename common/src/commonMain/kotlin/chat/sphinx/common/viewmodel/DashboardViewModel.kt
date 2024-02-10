package chat.sphinx.common.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import chat.sphinx.authentication.model.OnBoardStepHandler
import chat.sphinx.common.state.*
import chat.sphinx.concepts.network.query.version.NetworkQueryVersion
import chat.sphinx.concepts.socket_io.SocketIOManager
import chat.sphinx.database.core.SphinxDatabaseQueries
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.features.repository.util.deleteAll
import chat.sphinx.features.repository.util.upsertContact
import chat.sphinx.logger.d
import chat.sphinx.response.*
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.bridge.toBridgeAuthorizeMessage
import chat.sphinx.wrapper.bridge.toBridgeAuthorizeMessageOrNull
import chat.sphinx.wrapper.bridge.toBridgeSetBudgetMessageOrNull
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.NodeBalance
import chat.sphinx.wrapper.tribe.TribeJoinLink
import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val socketIOManager: SocketIOManager = SphinxContainer.networkModule.socketIOManager
    private val networkQueryVersion: NetworkQueryVersion = SphinxContainer.networkModule.networkQueryVersion

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

    private fun getRelayKeys() {
        viewModelScope.launch(dispatchers.io) {
            repositoryDashboard.getAndSaveTransportKey()
            repositoryDashboard.getOrCreateHMacKey(forceGet = true)
        }
    }

    private fun getPackageVersion(){
        val currentAppVersion = "1.0.27"

        viewModelScope.launch(dispatchers.mainImmediate) {
            networkQueryVersion.getAppVersions().collect { loadResponse ->
                when (loadResponse) {
                    is Response.Error -> {
                        _packageVersionAndUpgrade.value = Pair(currentAppVersion, false)
                    }
                    is Response.Success -> {
                        val serverHubVersion = loadResponse.value.kmm

                        currentAppVersion.replace(".", "").toIntOrNull()?.let { currentVersion ->
                            if (serverHubVersion > currentVersion) {
                                _packageVersionAndUpgrade.value = Pair(currentAppVersion, true)
                            }
                            else {
                                _packageVersionAndUpgrade.value = Pair(currentAppVersion, false)
                            }
                        }
                    }
                    is LoadResponse.Loading -> {

                    }
                }
            }
        }
    }

    private var screenInit: Boolean = false
    fun screenInit() {
        if (screenInit) {
            return
        } else {
            screenInit = true
        }
        getRelayKeys()
        networkRefresh()
        connectSocket()
        getPackageVersion()

        viewModelScope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.getAccountBalanceStateFlow().collect {
                _balanceStateFlow.value = it
            }
        }
    }

    private fun connectSocket() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            socketIOManager.connect()
        }
    }

    override fun windowGainedFocus(p0: WindowEvent?) {
        if (DashboardScreenState.screenState() == DashboardScreenType.Unlocked) {
            networkRefresh()
            connectSocket()
        }
    }

    override fun windowLostFocus(p0: WindowEvent?) { }

    private val _networkStateFlow: MutableStateFlow<LoadResponse<Boolean, ResponseError>> by lazy {
        MutableStateFlow(LoadResponse.Loading)
    }

    private val _restoreStateFlow: MutableStateFlow<RestoreProgress?> by lazy {
        MutableStateFlow(null)
    }

    val networkStateFlow: StateFlow<LoadResponse<Boolean, ResponseError>>
        get() = _networkStateFlow.asStateFlow()

    val restoreStateFlow: StateFlow<RestoreProgress?>
        get() = _restoreStateFlow.asStateFlow()

    private var jobNetworkRefresh: Job? = null


    fun networkRefresh() {
        if (jobNetworkRefresh?.isActive == true) {
            return
        }

        viewModelScope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.networkRefreshBalance.collect { }
        }

        jobNetworkRefresh = viewModelScope.launch(dispatchers.mainImmediate) {

            repositoryDashboard.networkRefreshLatestContacts.collect { response ->
                Exhaustive@
                when (response) {
                    is LoadResponse.Loading -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Error -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Success -> {
                        val restoreProgress = response.value

                        if (restoreProgress.restoring) {
                            _restoreStateFlow.value = restoreProgress
                        }
                    }
                }
            }

            if (_networkStateFlow.value is Response.Error) {
                jobNetworkRefresh?.cancel()
            }

            repositoryDashboard.networkRefreshMessages.collect { response ->
                Exhaustive@
                when (response) {
                    is Response.Success -> {
                        val restoreProgress = response.value

                        if (restoreProgress.restoring && restoreProgress.progress < 100) {
                            _restoreStateFlow.value = restoreProgress
                        } else {
                            _restoreStateFlow.value = null

                            _networkStateFlow.value = Response.Success(true)
                        }
                    }
                    is Response.Error -> {
                        _networkStateFlow.value = response
                    }
                    is LoadResponse.Loading -> {
                        _networkStateFlow.value = response
                    }
                }
            }

            if (_networkStateFlow.value is Response.Error) {
                jobNetworkRefresh?.cancel()
            }
        }
    }

    fun cancelRestore() {
        jobNetworkRefresh?.cancel()

        viewModelScope.launch(dispatchers.mainImmediate) {

            _networkStateFlow.value = Response.Success(true)
            _restoreStateFlow.value = null

            repositoryDashboard.didCancelRestore()
        }
    }

    fun clearDatabase() {
        coreDB.getSphinxDatabaseQueriesOrNull()?.let { queries: SphinxDatabaseQueries ->
            queries.transaction {
                deleteAll(queries)
            }
        }
    }
}