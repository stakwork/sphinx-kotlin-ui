package chat.sphinx.common.chatMesssageUI

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.UriHandler
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel

@Composable
actual fun URLPreview(
    linkPreview: ChatMessage.LinkPreview.HttpUrlPreview,
    chatViewModel: ChatViewModel,
    uriHandler: UriHandler
) {
}