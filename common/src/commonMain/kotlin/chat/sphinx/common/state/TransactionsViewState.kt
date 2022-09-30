package chat.sphinx.common.state

data class TransactionsViewState(
   val loadingTransactions: Boolean = true,
   val loadingMore: Boolean = false,
   var transactionsList: List<TransactionState?> = mutableListOf()
)
