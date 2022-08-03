package chat.sphinx.common.state

data class BackupKeysState(
   val restoreString: String? = null,
   val error: Boolean = false
)
