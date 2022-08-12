package chat.sphinx.common.chatMesssageUI

import androidx.compose.runtime.Composable
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.utils.linkify.LinkSpec

@Composable
actual fun NewContactPreview(
    chatMessage: ChatMessage,
    linkPreview: ChatMessage.LinkPreview.ContactPreview,
    chatViewModel: ChatViewModel
) {
}