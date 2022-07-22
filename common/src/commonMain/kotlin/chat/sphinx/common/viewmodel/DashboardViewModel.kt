package chat.sphinx.common.viewmodel

import chat.sphinx.common.state.ContactScreenState
import chat.sphinx.common.state.DashboardScreenType
import chat.sphinx.common.state.DashboardScreenState
import chat.sphinx.concepts.socket_io.SocketIOManager
import chat.sphinx.concepts.socket_io.SocketIOState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.dashboard.RestoreProgress
import chat.sphinx.wrapper.lightning.NodeBalance
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import kotlinx.coroutines.flow.collect

class DashboardViewModel: WindowFocusListener {
    val dispatchers = SphinxContainer.appModule.dispatchers
    val viewModelScope = SphinxContainer.appModule.applicationScope
    val sphinxNotificationManager = createSphinxNotificationManager()
    val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard
    val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    val socketIOManager: SocketIOManager = SphinxContainer.networkModule.socketIOManager

    private val _balanceStateFlow: MutableStateFlow<NodeBalance?> by lazy {
        MutableStateFlow(null)
    }

    val balanceStateFlow: StateFlow<NodeBalance?>
        get() = _balanceStateFlow.asStateFlow()

    private val _contactWindowStateFlow: MutableStateFlow<Pair<Boolean, ContactScreenState?>> by lazy {
        MutableStateFlow(Pair(false, null))
    }

    val contactWindowStateFlow: StateFlow<Pair<Boolean, ContactScreenState?>>
        get() = _contactWindowStateFlow.asStateFlow()

    fun toggleContactWindow(open: Boolean, screen: ContactScreenState?) {
        _contactWindowStateFlow.value = Pair(open, screen)
    }

    private val _qrWindowStateFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }

    val qrWindowStateFlow: StateFlow<Boolean>
        get() = _qrWindowStateFlow.asStateFlow()

    fun toggleQRWindow(open: Boolean) {
        _qrWindowStateFlow.value = open
    }

    private var screenInit: Boolean = false
    fun screenInit() {
        if (screenInit) {
            return
        } else {
            screenInit = true
        }

        if (SphinxContainer.authenticationModule.authenticationCoreManager.getEncryptionKey() != null) {
            DashboardScreenState.screenState(DashboardScreenType.Unlocked)
            networkRefresh()
        }

        networkRefresh()
        connectSocket()

        viewModelScope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.getAccountBalanceStateFlow().collect {
                _balanceStateFlow.value = it
            }
        }
    }

    private fun connectSocket() {
        viewModelScope.launch(dispatchers.mainImmediate) {
            socketIOManager.socketIOStateFlow.collect { state ->
                if (state is SocketIOState.Uninitialized ||
                    state is SocketIOState.Initialized.Disconnected ||
                    state is SocketIOState.Initialized.Closed) {

                    socketIOManager.connect()
                }
            }
        }
    }

    override fun windowGainedFocus(p0: WindowEvent?) {
        if (DashboardScreenState.screenState() == DashboardScreenType.Unlocked) {
            connectSocket()
            networkRefresh()
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
    private var jobPushNotificationRegistration: Job? = null


    fun networkRefresh() {
        if (jobNetworkRefresh?.isActive == true) {
            return
        }

        jobNetworkRefresh = viewModelScope.launch(dispatchers.mainImmediate) {
            contactRepository.networkRefreshContacts.collect { response ->
                Exhaustive@
                when (response) {
                    is LoadResponse.Loading -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Error -> {}
                    is Response.Success -> {}
                }
            }

            repositoryDashboard.networkRefreshBalance.collect { response ->
                Exhaustive@
                when (response) {
                    is LoadResponse.Loading -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Error -> {
                        _networkStateFlow.value = response
                    }
                    is Response.Success -> {}
                }
            }

            if (_networkStateFlow.value is Response.Error) {
                jobNetworkRefresh?.cancel()
            }

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

            // must occur after contacts have been retrieved such that
            // an account owner is available, otherwise it just suspends
            // until it is.
            if (jobPushNotificationRegistration == null) {
                jobPushNotificationRegistration = launch(dispatchers.mainImmediate) {
                    // TODO: Return push notifications...
//                    pushNotificationRegistrar.register().let { response ->
//                        Exhaustive@
//                        when (response) {
//                            is Response.Error -> {
//                                // TODO: Handle on the UI
//                            }
//                            is Response.Success -> {}
//                        }
//                    }
                }
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
}