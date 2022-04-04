package chat.sphinx.common.uistate

class ChatUIState {
}

sealed class ChatFilter {

    /**
     * Will use the current filter (if any) applied to the list of [DashboardChat]s.
     * */
    object UseCurrent: ChatFilter()

    /**
     * Will filter the list of [DashboardChat]s based on the provided [value]
     * */
    class FilterBy(val value: CharSequence): ChatFilter() {
        init {
            require(value.isNotEmpty()) {
                "ChatFilter.FilterBy cannot be empty. Use ClearFilter."
            }
        }
    }

    /**
     * Clears any applied filters.
     * */
    object ClearFilter: ChatFilter()
}
