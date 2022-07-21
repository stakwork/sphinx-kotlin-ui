package chat.sphinx.common.components.menu

import androidx.compose.material.*
import androidx.compose.runtime.*

@Composable
expect fun ChatActionMenu(showDialog:Boolean,callBack: (ChatActionMenuEnums) -> Unit)

enum class ChatActionMenuEnums {
     REQUEST, SEND,CANCEL
}

