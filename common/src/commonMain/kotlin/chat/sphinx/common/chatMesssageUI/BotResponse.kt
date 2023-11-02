package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import chat.sphinx.common.components.MessageMenu
import chat.sphinx.common.components.chat.KebabMenu
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

@Composable
fun BotResponse(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
){
    chatMessage.botResponse?.let { contentHtml ->
        val state = rememberWebViewStateWithHTMLData(
            contentHtml,
            null,
            "utf-8",
            "text/html",
            null
        )
    WebView(state)

    }
}