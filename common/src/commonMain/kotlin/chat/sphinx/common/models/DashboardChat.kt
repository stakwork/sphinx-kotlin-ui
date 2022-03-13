package chat.sphinx.common.models

import chat.sphinx.wrapper.*
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.getColorKey
import chat.sphinx.wrapper.chat.isConversation
import chat.sphinx.wrapper.chat.isTribeOwnedByAccount
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.getColorKey
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.invite.InviteStatus
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.MediaType

import kotlinx.coroutines.flow.Flow
import chat.sphinx.wrapper.invite.Invite as InviteWrapper
import chat.sphinx.common.Res as R
/**
 * [DashboardChat]s are separated into 2 categories:
 *  - [Active]: An active chat
 *  - [Inactive]: A contact without a conversation yet, or an Invite
 * */
sealed class DashboardChat {

    abstract val chatName: String?
    abstract val photoUrl: PhotoUrl?
    abstract val sortBy: Long

    abstract val unseenMessageFlow: Flow<Long?>?

    abstract fun getDisplayTime(today00: DateTime): String

    abstract fun getMessageText(): String

    abstract fun hasUnseenMessages(): Boolean

    abstract fun isEncrypted(): Boolean

    abstract fun getColorKey(): String?

    fun getColorKeyFor(contact: Contact?, chat: Chat?): String? {
        return contact?.getColorKey() ?: chat?.getColorKey()
    }

//    sealed class Active: DashboardChat() {
//
//        abstract val chat: Chat
//        abstract val message: Message?
//
//        open val owner: Contact? = null
//
//        override val sortBy: Long
//            get() {
//                val lastContentSeenDate = chat.contentSeenAt?.time
//                val lastMessageActionDate = message?.date?.time ?: chat.createdAt.time
//
//                if (lastContentSeenDate != null && lastContentSeenDate > lastMessageActionDate) {
//                    return lastContentSeenDate
//                }
//                return lastMessageActionDate
//            }
//
//        override fun getDisplayTime(today00: DateTime): String {
//            return message?.date?.chatTimeFormat(today00) ?: ""
//        }
//
//        fun isMessageSenderSelf(message: Message): Boolean =
//            message.sender == chat.contactIds.firstOrNull()
//
//        abstract fun getMessageSender(message: Message, withColon: Boolean = true): String
//
//        fun isMyTribe(owner: Contact?): Boolean =
//            chat.isTribeOwnedByAccount(owner?.nodePubKey)
//
//        override fun hasUnseenMessages(): Boolean {
//            val ownerId: ContactId? = chat.contactIds.firstOrNull()
//            val isLastMessageOutgoing = message?.sender == ownerId
//            val lastMessageSeen = message?.seen?.isTrue() ?: true
//            val chatSeen = chat.seen.isTrue()
//            return !lastMessageSeen && !chatSeen && !isLastMessageOutgoing
//        }
//
//        override fun isEncrypted(): Boolean {
//            return true
//        }
//
//        override fun getColorKey(): String? {
//            return getColorKeyFor(null, chat)
//        }
//
//        @ExperimentalStdlibApi
//        override fun getMessageText(): String {
//            val message: Message? = message
//            return when {
//                message == null -> {
//                    ""
//                }
//
//                message.status.isDeleted() -> {
//                    getString(R.string.last_message_description_message_deleted)
//                }
//                message.type.isInvoicePayment() -> {
//                    val amount: String = message.amount
//                        .asFormattedString(separator = ',', appendUnit = true)
//
//                    if (isMessageSenderSelf(message)) {
//                        getString(R.string.last_message_description_payment_sent, amount)
//                    } else {
//                        getString(R.string.last_message_description_payment_received, amount)
//                    }
//                }
//                message.type.isGroupJoin() -> {
//                    getString(
//                        R.string.last_message_description_has_join_tribe,
//                        getMessageSender(message, false)
//                    )
//                }
//                message.type.isGroupLeave() -> {
//                    getString(
//                        R.string.last_message_description_just_left_tribe,
//                        getMessageSender(message, false)
//                    )
//                }
//                message.type.isMemberRequest() -> {
//                    getString(
//                        R.string.last_message_description_wants_to_join,
//                        getMessageSender(message, false)
//                    )
//                }
//                message.type.isMemberReject() -> {
//                    if (isMyTribe(owner)) {
//                        getString(
//                            R.string.last_message_description_declined_request_from,
//                            getMessageSender(message, false)
//                        )
//                    } else {
//                        getString(R.string.last_message_description_admin_declined_request)
//                    }
//                }
//                message.type.isMemberApprove() -> {
//                    if (isMyTribe(owner)) {
//                        getString(
//                            R.string.last_message_description_approved_request_from,
//                            getMessageSender(message, false)
//                        )
//                    } else {
//                        getString(R.string.last_message_description_welcome_member)
//                    }
//                }
//                message.type.isGroupKick() -> {
//                    getString(R.string.last_message_description_group_kick)
//                }
//                message.type.isTribeDelete() -> {
//                    getString(R.string.last_message_description_tribe_deleted)
//                }
//                message.type.isBoost() -> {
//                    val amount: String = (message.feedBoost?.amount ?: message.amount)
//                        .asFormattedString(separator = ',', appendUnit = true)
//
//                    getString(
//                        R.string.last_message_description_boost,
//                        getMessageSender(message, true),
//                        amount
//                    )
//                }
//                message.messageDecryptionError -> {
//                    getString(R.string.decryption_error)
//                }
//                message.type.isMessage() -> {
//                    message.messageContentDecrypted?.value?.let { decrypted ->
//                        when {
//                            message.giphyData != null -> {
//                                getString(
//                                    R.string.last_message_description_gif_shared,
//                                    getMessageSender(message)
//                                )
//                            }
//                            message.feedBoost != null -> {
//                                val amount: String = (message.feedBoost?.amount ?: message.amount)
//                                    .asFormattedString(separator = ',', appendUnit = true)
//
//                                getString(
//                                    R.string.last_message_description_boost,
//                                    getMessageSender(message),
//                                    amount
//                                )
//                            }
//                            message.podcastClip != null -> {
//                                "${getMessageSender(message)}${message.podcastClip?.text ?: ""}"
//                            }
//                            message.isSphinxCallLink -> {
//                                getString(
//                                    R.string.last_message_description_call_invite,
//                                    getMessageSender(message)
//                                )
//                            }
//                            else -> {
//                                "${getMessageSender(message)}$decrypted"
//                            }
//                        }
//                    } ?: "${getMessageSender(message)}..."
//                }
//                message.type.isInvoice() -> {
//                    val amount: String = message.amount
//                        .asFormattedString(separator = ',', appendUnit = true)
//
//                    if (isMessageSenderSelf(message)) {
//                        getString(R.string.last_message_description_invoice_sent, amount)
//                    } else {
//                        getString(R.string.last_message_description_invoice_receive, amount)
//                    }
//
//                }
//                message.type.isDirectPayment() -> {
//                    val amount: String = message.amount
//                        .asFormattedString(separator = ',', appendUnit = true)
//
//                    if (isMessageSenderSelf(message)) {
//                        getString(R.string.last_message_description_payment_sent, amount)
//                    } else {
//                        getString(R.string.last_message_description_payment_received, amount)
//                    }
//                }
//                message.type.isAttachment() -> {
//                    message.messageMedia?.let { media ->
//                        when (val type = media.mediaType) {
//                            is MediaType.Audio -> {
//                                R.string.last_message_description_an_audio_clip
//                            }
//                            is MediaType.Image -> {
//                                if (type.isGif) {
//                                    R.string.last_message_description_a_gif
//                                } else {
//                                    R.string.last_message_description_an_image
//                                }
//                            }
//                            is MediaType.Pdf -> {
//                                R.string.last_message_description_a_pdf
//                            }
//                            is MediaType.Text -> {
//                                R.string.last_message_description_a_paid_message
//                            }
//                            is MediaType.Unknown -> {
//                                R.string.last_message_description_an_attachment
//                            }
//                            is MediaType.Video -> {
//                                R.string.last_message_description_a_video
//                            }
//                            else -> {
//                                null
//                            }
//                        }?.let { stringId ->
//                            val sentString = getString(R.string.last_message_description_sent)
//                            val element = getString(stringId)
//
//                            "${getMessageSender(message,false)} $sentString $element"
//                        }
//                    } ?: ""
//                }
//                else -> {
//                    ""
//                }
//            }
//        }
//
//        class Conversation(
//            override val chat: Chat,
//            override val message: Message?,
//            val contact: Contact,
//            override val unseenMessageFlow: Flow<Long?>,
//        ): Active() {
//
//            init {
//                require(chat.type.isConversation()) {
//                    """
//                    DashboardChat.Conversation is strictly for
//                    Contacts. Use DashboardChat.GroupOrTribe.
//                """.trimIndent()
//                }
//            }
//
//            override val chatName: String?
//                get() = contact.alias?.value
//
//            override val photoUrl: PhotoUrl?
//                get() = chat.photoUrl ?: contact.photoUrl
//
//            override fun getMessageSender(message: Message, withColon: Boolean): String {
//                if (isMessageSenderSelf(message)) {
//                    return getString(R.string.last_message_description_you) + if (withColon) ": " else ""
//                }
//
//                return contact.alias?.let { alias ->
//                    alias.value + if (withColon) ": " else ""
//                } ?: ""
//            }
//
//            override fun getColorKey(): String? {
//                return getColorKeyFor(contact, chat)
//            }
//        }
//
//        class GroupOrTribe(
//            override val chat: Chat,
//            override val message: Message?,
//            override val owner: Contact?,
//            override val unseenMessageFlow: Flow<Long?>,
//        ): Active() {
//
//            override val chatName: String?
//                get() = chat.name?.value
//
//            override val photoUrl: PhotoUrl?
//                get() = chat.photoUrl
//
//            override fun getMessageSender(message: Message, withColon: Boolean): String {
//                if (isMessageSenderSelf(message)) {
//                    return getString(R.string.last_message_description_you) + if (withColon) ": " else ""
//                }
//
//                return message.senderAlias?.let { alias ->
//                    alias.value + if (withColon) ": " else ""
//                } ?: ""
//            }
//
//            override fun getColorKey(): String? {
//                return getColorKeyFor(null, chat)
//            }
//
//        }
//    }

    /**
     * Inactive chats are for newly added contacts that are awaiting
     * messages to be sent (the Chat has not been created yet)
     * */
//    sealed class Inactive: DashboardChat() {
//
//        override fun getDisplayTime(today00: DateTime): String {
//            return ""
//        }
//
//        class Conversation(
//            val contact: Contact
//        ): Inactive() {
//
//            override val chatName: String?
//                get() = contact.alias?.value
//
//            override val photoUrl: PhotoUrl?
//                get() = contact.photoUrl
//
//            override val sortBy: Long
//                get() = contact.createdAt.time
//
//            override val unseenMessageFlow: Flow<Long?>?
//                get() = null
//
//            override fun getMessageText(): String {
//                return ""
//            }
//
//            override fun hasUnseenMessages(): Boolean {
//                return false
//            }
//
//            override fun isEncrypted(): Boolean {
//                return !(contact.rsaPublicKey?.value?.isEmpty() ?: true)
//            }
//
//            override fun getColorKey(): String? {
//                return getColorKeyFor(contact, null)
//            }
//
//        }
//
//        class Invite(
//            val contact: Contact,
//            val invite: InviteWrapper?
//        ): Inactive() {
//
//            override val chatName: String?
//                get() =  "Invite: ${contact.alias?.value ?: "Unknown"}"
//
//            override val photoUrl: PhotoUrl?
//                get() = contact.photoUrl
//
//            override val sortBy: Long
//                get() = Long.MAX_VALUE
//
//            override val unseenMessageFlow: Flow<Long?>?
//                get() = null
//
//            fun getChatName(): String {
//                val contactAlias = contact.alias?.value ?: getString(R.string.unknown)
//                return getString(R.string.last_message_description_invite, contactAlias)
//            }
//
//            override fun getMessageText(): String {
//
//                return when (invite?.status) {
//                    is InviteStatus.Pending -> {
//                        getString(
//                            R.string.last_message_description_looking_available_node,
//                            (contact.alias?.value ?: getString(R.string.unknown))
//                        )
//                    }
//                    is InviteStatus.Ready, InviteStatus.Delivered -> {
//                        getString(R.string.last_message_description_invite_ready)
//                    }
//                    is InviteStatus.InProgress -> {
//                        getString(
//                            R.string.last_message_description_invite_signing_on,
//                            getChatName()
//                        )
//                    }
//                    is InviteStatus.PaymentPending -> {
//                        getString(R.string.last_message_description_invite_tap_to_pay)
//                    }
//                    is InviteStatus.ProcessingPayment -> {
//                        getString(R.string.last_message_description_invite_payment_sent)
//                    }
//                    is InviteStatus.Complete -> {
//                        getString(R.string.last_message_description_invite_signup_complete)
//                    }
//                    is InviteStatus.Expired -> {
//                        getString(R.string.last_message_description_invite_expired)
//                    }
//
//                    null,
//                    is InviteStatus.Unknown -> {
//                        ""
//                    }
//                }
//            }
//
//            fun getInviteIconAndColor(): Pair<Int, Int>? {
//
//                return when (invite?.status) {
//                    is InviteStatus.Pending -> {
//                        Pair(R.string.material_icon_name_invite_pending, R.color.sphinxOrange)
//                    }
//                    is InviteStatus.Ready, InviteStatus.Delivered -> {
//                        Pair(R.string.material_icon_name_invite_ready, R.color.primaryGreen)
//                    }
//                    is InviteStatus.InProgress -> {
//                        Pair(R.string.material_icon_name_invite_in_progress, R.color.primaryBlue)
//                    }
//                    is InviteStatus.PaymentPending -> {
//                        Pair(R.string.material_icon_name_invite_payment_pending, R.color.secondaryText)
//                    }
//                    is InviteStatus.ProcessingPayment -> {
//                        Pair(R.string.material_icon_name_invite_payment_sent, R.color.secondaryText)
//                    }
//                    is InviteStatus.Complete -> {
//                        Pair(R.string.material_icon_name_invite_complete, R.color.primaryGreen)
//                    }
//                    is InviteStatus.Expired -> {
//                        Pair(R.string.material_icon_name_invite_expired, R.color.primaryRed)
//                    }
//
//                    null,
//                    is InviteStatus.Unknown -> {
//                        null
//                    }
//                }
//            }
//
//            fun getInvitePrice(): Sat? {
//                return invite?.price
//            }
//
//            override fun hasUnseenMessages(): Boolean {
//                return false
//            }
//
//            override fun isEncrypted(): Boolean {
//                return false
//            }
//
//            override fun getColorKey(): String? {
//                return getColorKeyFor(contact, null)
//            }
//        }
//    }
}
