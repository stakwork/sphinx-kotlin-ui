package chat.sphinx.common.chatMesssageUI

import androidx.compose.runtime.Composable
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.linkify.LinkSpec

@Composable
actual fun NewTribePreview(
    chatMessage: ChatMessage,
    linkPreview: ChatMessage.LinkPreview.TribeLinkPreview,
    chatViewModel: ChatViewModel
) {
}