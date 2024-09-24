package chat.sphinx.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.ProfileState
import chat.sphinx.common.state.TransactionType
import chat.sphinx.common.state.TransactionState
import chat.sphinx.common.state.TransactionsViewState
import chat.sphinx.concepts.network.query.chat.NetworkQueryChat
import chat.sphinx.concepts.network.query.message.model.TransactionDto
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.notifications.createSphinxNotificationManager
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.chat.isConversation
import chat.sphinx.wrapper.chat.isTribeNotOwnedByAccount
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.localDateTimeString
import chat.sphinx.wrapper.message.MessageUUID
import chat.sphinx.wrapper.message.SenderAlias
import chat.sphinx.wrapper.message.toMessageUUID
import chat.sphinx.wrapper.toDateTime
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class TransactionsViewModel {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    private val sphinxNotificationManager = createSphinxNotificationManager()
    private val contactRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).contactRepository
    private val chatRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).chatRepository
    private val messageRepository = SphinxContainer.repositoryModule(sphinxNotificationManager).messageRepository

    private var page: Int = 0
    private val itemsPerPage: Int = 50

    var transactionViewState: TransactionsViewState by mutableStateOf(initialState())

    private fun initialState(): TransactionsViewState = TransactionsViewState()

    private inline fun setTransactionsViewState(update: TransactionsViewState.() -> TransactionsViewState) {
        transactionViewState = transactionViewState.update()
    }

    init {
        scope.launch(dispatchers.mainImmediate) {
            loadTransactions()
        }
    }

    private suspend fun getOwner(): Contact {
        return contactRepository.accountOwner.value.let { contact ->
            if (contact != null) {
                contact
            } else {
                var resolvedOwner: Contact? = null
                try {
                    contactRepository.accountOwner.collect { ownerContact ->
                        if (ownerContact != null) {
                            resolvedOwner = ownerContact
                            throw Exception()
                        }
                    }
                } catch (e: Exception) {
                }
                delay(25L)

                resolvedOwner!!
            }
        }
    }

    private suspend fun loadTransactions() {
        // TODO V2 getPayments

//        networkQueryMessage.getPayments(
//            offset = page * itemsPerPage,
//            limit = itemsPerPage
//        ).collect() { loadResponse ->
//            when (loadResponse) {
//                is LoadResponse.Loading -> {}
//                is Response.Error -> {
//                    setTransactionsViewState {
//                        copy(
//                            loadingTransactions = false
//                        )
//                    }
//                }
//                is Response.Success -> {
//                    if (loadResponse.value.isNotEmpty()) {
//                        generateTransactionsStateList(loadResponse.value)
//                    } else {
//                        setTransactionsViewState {
//                            copy(
//                                loadingTransactions = false
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }

    private suspend fun generateTransactionsStateList(
        transactions: List<TransactionDto>

    ) {
        val owner = getOwner()
        val transactionsList = mutableListOf<TransactionState>()

        var chatsIdsMap: MutableMap<ChatId, ArrayList<Long>> = LinkedHashMap(transactions.size)
        var originalMessageUUIDsMap: MutableMap<MessageUUID, Long> = LinkedHashMap(transactions.size)

        var contactIdsMap: MutableMap<Long, ContactId> = LinkedHashMap(transactions.size)
        var contactAliasMap: MutableMap<Long, SenderAlias> = LinkedHashMap(transactions.size)

        for (transaction in transactions) {
            when {
                transaction.isIncomingWithSender(owner.id) -> {
                    transaction.getSenderId()?.let { senderId ->
                        contactIdsMap[transaction.id] = senderId
                    }
                    transaction.getSenderAlias()?.let { senderAlias ->
                        contactAliasMap[transaction.id] = senderAlias
                    }
                }
                transaction.isOutgoingWithReceiver(owner.id) -> {
                    transaction.getSenderId()?.let { senderId ->
                        contactIdsMap[transaction.id] = senderId
                    }
                }
                transaction.isOutgoingMessageBoost(owner.id) -> {
                    transaction.reply_uuid?.toMessageUUID()?.let { originalMessageUUID ->
                        originalMessageUUIDsMap[originalMessageUUID] = transaction.id
                    }
                }
                transaction.isPaymentInChat() -> {
                    transaction.getChatId()?.let { chatId ->
                        if (chatsIdsMap[chatId] == null) {
                            chatsIdsMap[chatId] = ArrayList(0)
                        }
                        chatsIdsMap[chatId]?.add(transaction.id)
                    }
                }
            }
        }

        val chatIds = chatsIdsMap.keys.map { it }
        chatRepository.getAllChatsByIds(chatIds).let { response ->
            response.forEach { chat ->
                if (
                    (chat.isTribeNotOwnedByAccount(owner.nodePubKey) || chat.isConversation()) &&
                    chat.contactIds.size == 2
                ) {
                    chatsIdsMap[chat.id]?.let { transactionIds ->
                        for (transactionId in transactionIds) {
                            contactIdsMap[transactionId] = chat.contactIds[1]
                        }
                    }
                }
            }
        }

        val originalMessageUUIDs = originalMessageUUIDsMap.keys.map { it }
        messageRepository.getAllMessagesByUUID(originalMessageUUIDs).let { response ->
            response.forEach { message ->
                originalMessageUUIDsMap[message.uuid]?.let { transactionId ->
                    contactIdsMap[transactionId] = message.sender

                    message.senderAlias?.let { senderAlias ->
                        contactAliasMap[transactionId] = senderAlias
                    }
                }
            }
        }

        val contactsMap: MutableMap<Long, Contact> = LinkedHashMap(transactions.size)
        val contactIds = contactIdsMap.values.map { it }

        contactRepository.getAllContactsByIds(contactIds).let { response ->
            response.forEach { contact ->
                contactsMap[contact.id.value] = contact
            }
        }

        for (transaction in transactions) {
        try {
            val senderId = contactIdsMap[transaction.id]
            val senderAlias: String? =
                contactAliasMap[transaction.id]?.value ?: contactsMap[senderId?.value]?.alias?.value

            val transactionAmount = transaction.amount.toString()
            val date = transaction.date.toDateTime()
            val dateString = date.localDateTimeString(DateTime.getFormateeemmddhmma())
            val failedTransaction = transaction.error_message


            if (!failedTransaction.isNullOrBlank()) {
                transactionsList.add(
                    TransactionState(
                        amount = transactionAmount,
                        date = dateString,
                        senderReceiverName = senderAlias ?: "-",
                        transactionType = TransactionType.Failed,
                        failedTransactionMessage = failedTransaction
                    )
                )
            } else if (transaction.sender == owner.id.value) transactionsList.add(
                TransactionState(
                    amount = transactionAmount,
                    date = dateString,
                    senderReceiverName = senderAlias ?: "-",
                    transactionType = TransactionType.Outgoing,
                    failedTransactionMessage = null

                )
            ) else {
                transactionsList.add(
                    TransactionState(
                        amount = transactionAmount,
                        date = dateString,
                        senderReceiverName = senderAlias ?: "-",
                        transactionType = TransactionType.Incoming,
                        failedTransactionMessage = null
                    )
                )

            }
        } catch (_: Exception){
        }
    }

        val list = transactionViewState.transactionsList.toMutableList()
        list.addAll(transactionsList)

        setTransactionsViewState {
            copy(
                transactionsList = list,
                loadingTransactions = false,
                loadingMore = false
            )
        }
    }

    fun loadMoreTransactions() {
        if (transactionViewState.loadingTransactions || transactionViewState.loadingMore) {
            return
        }
        if (transactionViewState.transactionsList.size % itemsPerPage != 0) {
            return
        }
        setTransactionsViewState {
            copy(
                loadingMore = true
            )
        }
        page += 1

        scope.launch(dispatchers.mainImmediate) {
            loadTransactions()

        }
    }
}


