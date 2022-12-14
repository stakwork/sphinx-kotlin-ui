package chat.sphinx.common.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class AliasMatcherState(
    val isOn:  MutableState<Boolean> = mutableStateOf(false),
    val inputText: MutableState<String> = mutableStateOf(""),
    val suggestedAliasList: MutableState<List<String>> = mutableStateOf(listOf("")),
    val focus: MutableState<Boolean> = mutableStateOf(false)
    )
