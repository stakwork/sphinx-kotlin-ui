package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import chat.sphinx.common.components.MessageMenu
import chat.sphinx.common.components.chat.KebabMenu
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import com.multiplatform.webview.setting.PlatformWebSettings
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData

@Composable
fun BotResponse(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel
) {
    chatMessage.botResponse?.let { botResponse ->

        val textColorString = "#FFFFFF"
        val backgroundColorString = "black"

        val htmlContentPadding = "padding: 16px;"

        val htmlPrefix = "<head><meta name=\"viewport\" content=\"width=device-width, height=device-height, shrink-to-fit=YES\"></head><body style=\"font-family: 'Roboto', sans-serif; color: $textColorString; margin:0px !important; ${htmlContentPadding} background: $backgroundColorString;\"><div id=\"bot-response-container\" style=\"background: $backgroundColorString;\">"
        val htmlSuffix = "</div></body>"
        val contentHtml = htmlPrefix + botResponse + htmlSuffix

        val webViewState = rememberWebViewStateWithHTMLData(
            contentHtml,
            null,
            "utf-8",
            "text/html",
            null
        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp) // Padding around the Column
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                WebView(
                    state = webViewState,
                    modifier = Modifier
                        .height(280.dp)
                        .width(280.dp)
                )
            }
        }
    }
}

@Composable
internal fun WebViewSample() {
    MaterialTheme {
        val webViewState = rememberWebViewState("https://github.com/KevinnZou/compose-webview-multiplatform")
        Column(Modifier.fillMaxSize()) {
            val text = webViewState.let {
                "${it.pageTitle ?: ""} ${it.loadingState} ${it.lastLoadedUrl ?: ""}"
            }
            Text(text)
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}
