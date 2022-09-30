package chat.sphinx.common.state

import chat.sphinx.response.LoadResponse
import chat.sphinx.response.ResponseError

data class TransactionState(
    val amount: String = "",
    val date: String = "",
    val senderReceiverName: String = "",
    val transactionType: TransactionType? = null,
)
sealed class TransactionType() {

    object Outgoing: TransactionType()
    object Incoming: TransactionType()
}
