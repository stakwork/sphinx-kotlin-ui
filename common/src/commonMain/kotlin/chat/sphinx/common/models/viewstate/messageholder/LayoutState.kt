package chat.sphinx.common.models.viewstate.messageholder

import chat.sphinx.wrapper.chat.ChatType
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.lightning.LightningNodeDescriptor
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.lightning.unit
import chat.sphinx.wrapper.message.MessageId
import chat.sphinx.wrapper.message.MessageUUID
import chat.sphinx.wrapper.tribe.TribeJoinLink
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.message.MessageType
import chat.sphinx.wrapper.message.PodcastClip as PodcastClipObject
import chat.sphinx.wrapper.message.PurchaseStatus
import chat.sphinx.wrapper.message.media.MessageMedia
import java.io.File

sealed class LayoutState private constructor() {

    data class MessageStatusHeader(
        val senderName: String?,
        val colorKey: String,
        val showSent: Boolean,
        val showSendingIcon: Boolean,
        val showBoltIcon: Boolean,
        val showFailedContainer: Boolean,
        val showLockIcon: Boolean,
        val timestamp: String,
    ): LayoutState() {
        val showReceived: Boolean
            get() = !showSent
    }

    data class InvoiceExpirationHeader(
        val showExpirationReceivedHeader: Boolean,
        val showExpirationSentHeader: Boolean,
        val showExpiredLabel: Boolean,
        val showExpiresAtLabel: Boolean,
        val expirationTimestamp: String?,
    ): LayoutState()

    data class GroupActionIndicator(
        val actionType: MessageType.GroupAction,
        val chatType: ChatType?,
        val isAdminView: Boolean,
        val subjectName: String?,
    ): LayoutState()

    data class DeletedOrFlaggedMessage(
        val gravityStart: Boolean,
        val deleted: Boolean,
        val flagged: Boolean,
        val timestamp: String,
    ): LayoutState()

    data class InvoicePayment(
        val showSent: Boolean,
        val paymentDateString: String,
    ): LayoutState() {
        val showReceived: Boolean
            get() = !showSent
    }

    sealed class Bubble private constructor(): LayoutState() {

        sealed class ContainerFirst private constructor(): Bubble() {

            data class ReplyMessage(
                val showSent: Boolean,
                val sender: String,
                val colorKey: String,
                val text: String,
                val isAudio: Boolean,
                val url: String?,
                val media: MessageMedia?,
            ): ContainerFirst() {
                val showReceived: Boolean
                    get() = !showSent
            }

        }

        sealed class ContainerSecond private constructor(): Bubble() {

            data class PaidMessageSentStatus(
                val amount: Sat,
                val purchaseStatus: PurchaseStatus?,
            ): ContainerSecond() {
                val amountText: String
                    get() = amount.asFormattedString(appendUnit = true)
            }

            data class DirectPayment(
                val showSent: Boolean,
                val amount: Sat
            ): ContainerSecond() {
                val showReceived: Boolean
                    get() = !showSent

                val unitLabel: String
                    get() = amount.unit
            }

            data class Invoice(
                val showSent: Boolean,
                val amount: Sat,
                val text: String,
                val showPaidInvoiceBottomLine: Boolean,
                val hideBubbleArrows: Boolean,
                val showPayButton: Boolean,
                val showDashedBorder: Boolean,
                val showExpiredLayout: Boolean,
            ): ContainerSecond() {
                val showReceived: Boolean
                    get() = !showSent

                val unitLabel: String
                    get() = amount.unit
            }

            sealed class AudioAttachment: ContainerSecond() {

                data class FileAvailable(
                    val messageId: MessageId,
                    val file: File
                ): AudioAttachment()

                data class FileUnavailable(
                    val messageId: MessageId,
                    val showPaidOverlay: Boolean
                ): AudioAttachment()
            }

            data class ImageAttachment(
                val url: String,
                val media: MessageMedia?,
                val showPaidOverlay: Boolean
            ): ContainerSecond()

            sealed class VideoAttachment : ContainerSecond()  {
                data class FileAvailable(val file: File): VideoAttachment()
                data class FileUnavailable(val showPaidOverlay: Boolean): VideoAttachment()
            }

            data class PodcastBoost(
                val amount: Sat,
            ): ContainerSecond()

            data class CallInvite(
                val videoButtonVisible: Boolean
            ): ContainerSecond()

            data class BotResponse(
                val html: String
            ): ContainerSecond()

            data class PodcastClip(
                val messageId: MessageId,
                val messageUUID: MessageUUID?,
                val podcastClip: PodcastClipObject,
            ): ContainerSecond()

            // FileAttachment
            // Invoice
        }

        sealed class ContainerThird private constructor(): Bubble() {

            data class UnsupportedMessageType(
                val messageType: MessageType,
                val gravityStart: Boolean,
            ): ContainerThird()

            data class Message(
                val text: String
            ): ContainerThird()

            data class PaidMessage(
                val showSent: Boolean,
                val purchaseStatus: PurchaseStatus?
            ): ContainerThird()
        }

        sealed class ContainerFourth private constructor(): Bubble() {

            data class Boost(
                val showSent: Boolean,
                val boostedByOwner: Boolean,
                val senders: Set<BoostSenderHolder>,
                private val totalAmount: Sat,
            ): ContainerFourth() {
                val amountText: String
                    get() = totalAmount.asFormattedString()

                val amountUnitLabel: String
                    get() = totalAmount.unit

                // will be gone if null is returned
                val numberUniqueBoosters: Int?
                    get() = if (senders.size > 1) {
                        senders.size
                    } else {
                        null
                    }
            }

            data class PaidMessageReceivedDetails(
                val amount: Sat,
                val purchaseStatus: PurchaseStatus,
                val showStatusIcon: Boolean,
                val showProcessingProgressBar: Boolean,
                val showStatusLabel: Boolean,
                val showPayElements: Boolean,
            ): ContainerFourth() {
                val amountText: String
                    get() = amount.asFormattedString(appendUnit = true)
            }
        }
    }
}

data class BoostSenderHolder(
    val photoUrl: PhotoUrl?,
    val alias: ContactAlias?,
    val colorKey: String,
)
