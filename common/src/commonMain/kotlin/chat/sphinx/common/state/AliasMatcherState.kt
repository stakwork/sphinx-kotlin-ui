package chat.sphinx.common.state

data class AliasMatcherState(
    val isOn:  Boolean = false,
    val inputText: String = "",
    val suggestedAliasList: List<String> = listOf(""),
    val selectedItem: Int = 0,
)
