package chat.sphinx.common.models

import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.invoiceExpirationTimeFormat
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.isAudio
import chat.sphinx.wrapper.message.media.isImage
import chat.sphinx.wrapper.message.media.isSphinxText
import chat.sphinx.wrapper.message.media.isVideo

class ChatMessage(
    val chat: Chat,
    val message: Message
) {

    val messageUISpacerWidth: Int by lazy {
        message.retrieveTextToShow()?.let { messageText ->
            when {
                messageText.length > 100 -> 20
                messageText.length > 50 -> 30
                else -> 40
            }
        } ?: 40
    }


    val isSent: Boolean by lazy {
        message.sender == chat.contactIds.firstOrNull()
    }

    val isReceived: Boolean by lazy {
        !isSent
    }

    val isDeleted: Boolean by lazy {
        message.status.isDeleted()
    }

    val isFlagged: Boolean by lazy {
        message.isFlagged && !isDeleted
    }
    val showSendingIcon: Boolean by lazy {
        isSent && message.id.isProvisionalMessage && message.status.isPending()
    }

    val showLockIcon: Boolean by lazy {
        message.messageContentDecrypted != null || message.messageMedia?.mediaKeyDecrypted != null
    }

    val showBoltIcon: Boolean by lazy {
        isSent && (message.status.isReceived() || message.status.isConfirmed())
    }

    val showFailedContainer: Boolean by lazy {
        isSent && message.status.isFailed()
    }

    private val unsupportedMessageTypes: List<MessageType> by lazy {
        listOf(
            MessageType.Attachment,
            MessageType.Payment,
            MessageType.GroupAction.TribeDelete,
        )
    }

    val unsupportedMessageType: String? by lazy {
        if (
            unsupportedMessageTypes.contains(message.type) && message.messageMedia?.mediaType?.isSphinxText != true &&
            message.messageMedia?.mediaType?.isImage != true && message.messageMedia?.mediaType?.isAudio != true &&
            message.messageMedia?.mediaType?.isVideo != true
        ) {
            "${message.type} messages are unsupported"
        } else {
            null
        }
    }

    val invoiceExpirationHeader: String? by lazy {
        if (message.type.isInvoice() && !message.status.isDeleted()) {
            if (message.isExpiredInvoice) {
                "REQUEST EXPIRED"
            } else {
                message.expirationDate?.invoiceExpirationTimeFormat()?.let {
                    "EXPIRES AT: $it"
                }
            }
        } else {
            null
        }
    }
}