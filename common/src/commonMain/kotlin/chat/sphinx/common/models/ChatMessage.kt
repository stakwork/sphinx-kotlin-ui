package chat.sphinx.common.models

import androidx.compose.runtime.MutableState
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.concepts.meme_input_stream.MemeInputStreamHandler
import chat.sphinx.concepts.meme_server.MemeServerTokenHandler
import chat.sphinx.logger.e
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatType
import chat.sphinx.wrapper.chat.isConversation
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.invoiceExpirationTimeFormat
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.*
import java.io.FileInputStream
import java.io.InputStream

class ChatMessage(
    val chat: Chat,
    val contact: Contact?,
    val message: Message,
    accountOwner: () -> Contact,
    val boostMessage: () -> Unit,
    val flagMessage: () -> Unit,
    val deleteMessage: () -> Unit,
//    val replyToMessageAction: () -> Unit
) {
    fun setAsReplyToMessage(editMessageState: EditMessageState) {
        editMessageState.replyToMessage.value = this
    }

    val replyToMessageSenderAliasPreview: String by lazy {
        val senderAlias = when {
            message.sender == chat.contactIds.firstOrNull() -> {
                accountOwner().alias?.value ?: ""
            }
            else -> {
                message.senderAlias?.value ?: ""
            }
        }

        senderAlias
    }

    val replyToMessageTextPreview: String by lazy {
        val messageMediaText = if (message.messageMedia != null) "attachment" else ""
        message.retrieveTextToShow() ?: messageMediaText
    }

    val messageUISpacerWidth: Int by lazy {
        message.retrieveTextToShow()?.let { messageText ->
            when {
                messageText.length > 100 -> 40
                messageText.length > 50 -> 50
                else -> 60
            }
        } ?: 40
    }

    val isAdminView = if (chat.ownerPubKey == null || accountOwner().nodePubKey == null) {
        false
    } else {
        chat.ownerPubKey == accountOwner().nodePubKey
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

    val groupActionSubjectName: String = message.senderAlias?.value ?: ""
    val groupActionLabelText: String? = when (message.type) {
        MessageType.GroupAction.Join -> {
            if (chat.type == ChatType.Tribe) {
                "$groupActionSubjectName has joined the tribe"
            } else {
                "$groupActionSubjectName has joined the group"
            }
        }
        MessageType.GroupAction.Leave -> {
            if (chat.type == ChatType.Tribe) {
                "$groupActionSubjectName has left the tribe"
            } else {
                "$groupActionSubjectName has left the group"
            }
        }
        MessageType.GroupAction.MemberApprove -> {
            if (chat.type == ChatType.Tribe) {
                "Welcome! You are now a member"
            } else {
                null
            }
        }
        else -> {
            null
        }
    }


}