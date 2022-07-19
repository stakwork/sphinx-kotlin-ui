package chat.sphinx.common.models

import chat.sphinx.common.state.EditMessageState
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatType
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.invoiceExpirationTimeFormat
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.*
import androidx.compose.ui.graphics.Color
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.isConversation
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.contact.toContactAlias
import chat.sphinx.wrapper.lightning.Sat

class ChatMessage(
    val chat: Chat,
    val contact: Contact?,
    val message: Message,
    val colors: Map<Long, Int>,
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
            contact != null -> {
                contact.alias?.value ?: ""
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

    val replyToMessageColor: Int? by lazy {
        colors[message.id.value]
    }

    val boostsLayoutState: BoostLayoutState? by lazy {
        if (message == null) {
            null
        } else {
            message.reactions?.let { nnReactions ->
                if (nnReactions.isEmpty()) {
                    null
                } else {
                    val set: MutableSet<BoostSenderHolder> = LinkedHashSet(0)
                    var total: Long = 0
                    var boostedByOwner = false
                    val owner = accountOwner()

                    for (reaction in nnReactions) {
                        if (reaction.sender == owner.id) {
                            boostedByOwner = true

                            set.add(
                                BoostSenderHolder(
                                    chat.myPhotoUrl ?: owner.photoUrl,
                                    chat.myAlias?.value?.toContactAlias() ?: owner.alias,
                                    colors[reaction.id.value]
                                )
                            )
                        } else {
                            if (chat.type.isConversation()) {
                                set.add(
                                    BoostSenderHolder(
                                        contact?.photoUrl,
                                        contact?.alias,
                                        colors[reaction.id.value]
                                    )
                                )
                            } else {
                                set.add(
                                    BoostSenderHolder(
                                        reaction.senderPic,
                                        reaction.senderAlias?.value?.toContactAlias(),
                                        colors[reaction.id.value]
                                    )
                                )
                            }
                        }
                        total += reaction.amount.value
                    }

                    BoostLayoutState(
                        showSent = this.isSent,
                        boostedByOwner = boostedByOwner,
                        senders = set,
                        totalAmount = Sat(total),
                    )
                }
            }
        }
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

    data class BoostLayoutState(
        val showSent: Boolean,
        val boostedByOwner: Boolean,
        val senders: Set<BoostSenderHolder>,
        val totalAmount: Sat,
    )

    data class BoostSenderHolder(
        val photoUrl: PhotoUrl?,
        val alias: ContactAlias?,
        val color: Int?,
    )
}