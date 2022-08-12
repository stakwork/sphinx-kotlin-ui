package chat.sphinx.common.viewmodel.dashboard

import androidx.annotation.ColorInt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.ChatDetailData
import chat.sphinx.common.state.ChatDetailState
import chat.sphinx.common.state.ChatListData
import chat.sphinx.common.state.ChatListState
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.utils.SphinxDispatchers
import chat.sphinx.utils.UserColorsHelper
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.getColorKey
import chat.sphinx.wrapper.chat.isConversation
import chat.sphinx.wrapper.contact.*
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.invite.Invite
import chat.sphinx.wrapper.lightning.NodeBalance
import chat.sphinx.wrapper.message.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import theme.badge_red
import theme.primary_green
import utils.getRandomColorRes

@Suppress("NOTHING_TO_INLINE")
private inline fun List<DashboardChat>.filterDashboardChats(
    filter: CharSequence
): List<DashboardChat> =
    filter {
        it.chatName?.contains(filter, ignoreCase = true) == true
    }

class ChatListViewModel {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    private var dashboardChats: ArrayList<DashboardChat> = ArrayList()
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val repositoryDashboard = SphinxContainer.repositoryModule(sphinxNotificationManager).repositoryDashboard

    private val colorsHelper = UserColorsHelper(SphinxContainer.appModule.dispatchers)

    private val _contactsStateFlow: MutableStateFlow<List<Contact>> by lazy {
        MutableStateFlow(emptyList())
    }

    private val _accountOwnerStateFlow: MutableStateFlow<Contact?> by lazy {
        MutableStateFlow(null)
    }

    private val accountOwnerStateFlow: StateFlow<Contact?>
        get() = _accountOwnerStateFlow.asStateFlow()

    private suspend fun getAccountBalance(): StateFlow<NodeBalance?> =
        repositoryDashboard.getAccountBalanceStateFlow()

    var searchText: MutableState<String> = mutableStateOf("")

    private var contactsCollectionInitialized: Boolean = false
    private var chatsCollectionInitialized: Boolean = false

    private val collectionLock = Mutex()
    private val updateDashboardChatLock = Mutex()

    init {
        scope.launch(dispatchers.mainImmediate) {
            repositoryDashboard.getAllNotBlockedContacts.distinctUntilChanged().collect { contacts ->
                updateChatListContacts(contacts)
            }
        }

        scope.launch(dispatchers.mainImmediate) {
            delay(25L)

            repositoryDashboard.getAllChatsFlow.distinctUntilChanged().collect { chats ->
                collectionLock.withLock {
                    chatsCollectionInitialized = true

                    val newList = ArrayList<DashboardChat>(chats.size)
                    val contactsAdded = mutableListOf<ContactId>()

                    withContext(dispatchers.default) {
                        for (chat in chats) {
                            val message: Message? = chat.latestMessageId?.let {
                                repositoryDashboard.getMessageById(it).firstOrNull()
                            }

                            if (chat.type.isConversation()) {
                                val contactId: ContactId = chat.contactIds.lastOrNull() ?: continue

                                val contact: Contact = repositoryDashboard.getContactById(contactId)
                                    .firstOrNull() ?: continue

                                if (!contact.isBlocked()) {
                                    contactsAdded.add(contactId)

                                    newList.add(
                                        DashboardChat.Active.Conversation(
                                            chat,
                                            message,
                                            contact,
                                            getColorFor(contact, chat),
                                            repositoryDashboard.getUnseenMessagesByChatId(chat.id),
                                        )
                                    )
                                }
                            } else {
                                newList.add(
                                    DashboardChat.Active.GroupOrTribe(
                                        chat,
                                        message,
                                        accountOwnerStateFlow.value,
                                        getColorFor(null, chat),
                                        repositoryDashboard.getUnseenMessagesByChatId(chat.id)
                                    )
                                )
                            }
                        }
                    }

                    if (contactsCollectionInitialized) {
                        withContext(dispatchers.default) {
                            for (contact in _contactsStateFlow.value) {

                                if (!contactsAdded.contains(contact.id)) {
                                    if (contact.isInviteContact()) {
                                        var contactInvite: Invite? = null

                                        contact.inviteId?.let { inviteId ->
                                            contactInvite = withContext(dispatchers.io) {
                                                repositoryDashboard.getInviteById(inviteId).firstOrNull()
                                            }
                                        }

                                        if (contactInvite != null) {
                                            newList.add(
                                                DashboardChat.Inactive.Invite(
                                                    contact,
                                                    contactInvite!!,
                                                    getColorFor(contact, null)
                                                )
                                            )
                                            continue
                                        }
                                    }
                                    newList.add(
                                        DashboardChat.Inactive.Conversation(contact, getColorFor(contact, null))
                                    )
                                }

                            }
                        }
                    }
                    dashboardChats = ArrayList(newList.sortedByDescending { it.sortBy })
                    filterChats(searchText.value)
                }
            }
        }

        scope.launch(dispatchers.mainImmediate) {
            delay(50L)
            repositoryDashboard.getAllInvites.distinctUntilChanged().collect {
                updateChatListContacts(_contactsStateFlow.value)
            }
        }
    }

    fun filterChats(filter: String) {
        searchText.value = filter

        val currentChatListState = ChatListState.screenState()

        if (filter.isEmpty()) {
            ChatListState.screenState(
                ChatListData.PopulatedChatListData(
                    dashboardChats,
                    (currentChatListState as? ChatListData.PopulatedChatListData)?.selectedDashboardId
                )
            )
        } else {
            val filteredChats = dashboardChats.filterDashboardChats(
                filter
            )

            ChatListState.screenState(
                ChatListData.PopulatedChatListData(
                    filteredChats,
                    (currentChatListState as? ChatListData.PopulatedChatListData)?.selectedDashboardId
                )
            )
        }
    }

    fun chatRowSelected(dashboardChat: DashboardChat) {
        (ChatListState.screenState() as? ChatListData.PopulatedChatListData)?.let { currentState ->
            ChatListState.screenState(
                ChatListData.PopulatedChatListData(
                    currentState.dashboardChats,
                    dashboardChat.dashboardChatId
                )
            )
        }

        ChatDetailState.screenState(
            when (dashboardChat) {
                is DashboardChat.Active.Conversation -> {
                    ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail(
                        dashboardChat.chat.id,
                        dashboardChat.contact.id,
                        dashboardChat
                    )
                }
                is DashboardChat.Active.GroupOrTribe -> {
                    ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail(
                        dashboardChat.chat.id,
                        dashboardChat
                    )
                }
                is DashboardChat.Inactive.Conversation -> {
                    ChatDetailData.SelectedChatDetailData.SelectedContactDetail(
                        dashboardChat.contact.id,
                        dashboardChat
                    )
                }
                else -> ChatDetailData.EmptyChatDetailData
            }
        )
    }

    private suspend fun updateChatListContacts(contacts: List<Contact>) {
        collectionLock.withLock {
            contactsCollectionInitialized = true

            if (contacts.isEmpty()) {
                return@withLock
            }

            val newList = ArrayList<Contact>(contacts.size)
            val contactIds = ArrayList<ContactId>(contacts.size)

            withContext(dispatchers.default) {
                for (contact in contacts) {
                    if (contact.isOwner.isTrue()) {
                        _accountOwnerStateFlow.value = contact
                        continue
                    }

                    contactIds.add(contact.id)
                    newList.add(contact)
                }
            }

            _contactsStateFlow.value = newList.toList()

            // Don't push update to chat view state, let it's collection do it.
            if (!chatsCollectionInitialized) {
                return@withLock
            }

            withContext(dispatchers.default) {
                val currentChats: MutableList<DashboardChat> =
                    when (val populatedChats = ChatListState.screenState()) {
                        is ChatListData.PopulatedChatListData -> populatedChats.dashboardChats.toMutableList()
                        else -> mutableListOf()
                    }
                val chatContactIds = mutableListOf<ContactId>()

                var updateChatViewState = false
                for (chat in currentChats.toList()) {

                    val contact: Contact? = when (chat) {
                        is DashboardChat.Active.Conversation -> {
                            chat.contact
                        }
                        is DashboardChat.Active.GroupOrTribe -> {
                            null
                        }
                        is DashboardChat.Inactive.Conversation -> {
                            chat.contact
                        }
                        is DashboardChat.Inactive.Invite -> {
                            chat.contact
                        }
                    }

                    contact?.let {
                        chatContactIds.add(it.id)
                        // if the id of the currently displayed chat is not contained
                        // in the list collected here, it's either a new contact w/o
                        // a chat, or a contact that was deleted which we need to remove
                        // from the list of chats.

                        if (!contactIds.contains(it.id)) {
                            //Contact deleted
                            updateChatViewState = true
                            currentChats.remove(chat)
                            chatContactIds.remove(it.id)
                        }

                        if (repositoryDashboard.updatedContactIds.contains(it.id)) {
                            //Contact updated
                            currentChats.remove(chat)
                            chatContactIds.remove(it.id)
                        }
                    }
                }

                for (contact in _contactsStateFlow.value) {
                    //Contact added
                    if (!chatContactIds.contains(contact.id)) {
                        updateChatViewState = true

                        if (contact.isInviteContact()) {
                            var contactInvite: Invite? = null

                            contact.inviteId?.let { inviteId ->
                                contactInvite = withContext(dispatchers.io) {
                                    repositoryDashboard.getInviteById(inviteId).firstOrNull()
                                }
                            }
                            if (contactInvite != null) {
                                currentChats.add(
                                    DashboardChat.Inactive.Invite(
                                        contact,
                                        contactInvite!!,
                                        getColorFor(contact, null)
                                    )
                                )
                                continue
                            }
                        }

                        var updatedContactChat: DashboardChat =
                            DashboardChat.Inactive.Conversation(contact, getColorFor(contact, null))

                        for (chat in currentChats.toList()) {
                            if (chat is DashboardChat.Active.Conversation) {
                                if (chat.contact.id == contact.id) {
                                    updatedContactChat = DashboardChat.Active.Conversation(
                                        chat.chat,
                                        chat.message,
                                        contact,
                                        getColorFor(contact, chat.chat),
                                        chat.unseenMessageFlow
                                    )
                                }
                            }
                        }

                        if (updatedContactChat is DashboardChat.Inactive.Conversation) {
                            //Contact unblocked
                            repositoryDashboard.getConversationByContactIdFlow(contact.id)
                                .firstOrNull()?.let { contactChat ->
                                val message: Message? = contactChat.latestMessageId?.let {
                                    repositoryDashboard.getMessageById(it).firstOrNull()
                                }

                                updatedContactChat = DashboardChat.Active.Conversation(
                                    contactChat,
                                    message,
                                    contact,
                                    getColorFor(contact, contactChat),
                                    repositoryDashboard.getUnseenMessagesByChatId(contactChat.id)
                                )
                            }
                        }

                        currentChats.add(updatedContactChat)
                    }
                }

                if (updateChatViewState) {
                    dashboardChats = ArrayList(currentChats.sortedByDescending { it.sortBy })
                    filterChats(searchText.value)

                    repositoryDashboard.updatedContactIds = mutableListOf()
                }
            }
        }
    }

    fun payForInvite(invite: Invite) {
        scope.launch(dispatchers.mainImmediate) {
            getAccountBalance().firstOrNull()?.let { balance ->
                if (balance.balance.value < (invite.price?.value ?: 0)) {
                    toast("Can't pay invite. Balance is too low", badge_red)
                    return@launch
                }
            }

            confirm(
                "Pay Invite",
                "Are you sure you want to pay for this invite?"
            ) {
                scope.launch(dispatchers.mainImmediate) {
                    repositoryDashboard.payForInvite(invite)
                }
            }
        }
    }

    fun deleteInvite(invite: Invite) {
        confirm(
            "Delete Invite",
            "Are you sure you want to delete this invite?"
        ) {
            scope.launch(dispatchers.mainImmediate) {
                repositoryDashboard.deleteInvite(invite)
            }
        }
    }

    private fun toast(
        message: String,
        color: Color = primary_green,
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

    private fun confirm(
        title: String,
        message: String,
        callback: () -> Unit
    ) {
        scope.launch(dispatchers.mainImmediate) {
            sphinxNotificationManager.confirmAlert(
                "Sphinx",
                title,
                message,
                callback
            )
        }
    }

    @ColorInt
    suspend fun getColorFor(
        contact: Contact?,
        chat: Chat?
    ): Int? {
        (contact?.getColorKey() ?: chat?.getColorKey())?.let { colorKey ->
            return colorsHelper.getColorIntForKey(
                colorKey,
                Integer.toHexString(getRandomColorRes().hashCode())
            )
        }
        return null
    }
}