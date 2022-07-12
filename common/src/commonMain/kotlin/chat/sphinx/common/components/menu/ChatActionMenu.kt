package chat.sphinx.common.components.menu

import androidx.compose.material.*
import androidx.compose.runtime.*

@Composable
expect fun ChatActionMenu(callBack: (ChatActionMenuEnums) -> Unit)

enum class ChatActionMenuEnums {
    LIBRARY, GIF, FILE, PAID_MESSAGE, REQUEST, SEND
}

