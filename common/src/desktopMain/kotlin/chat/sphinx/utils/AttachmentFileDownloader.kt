package chat.sphinx.utils

import chat.sphinx.concepts.meme_input_stream.MemeInputStreamHandler
import chat.sphinx.concepts.meme_server.MemeServerTokenHandler
import chat.sphinx.wrapper.message.Message
import chat.sphinx.wrapper.message.isAttachmentAvailable
import chat.sphinx.wrapper.message.retrieveImageUrlAndMessageMedia
import chat.sphinx.wrapper.message.retrieveVideoUrlAndMessageMedia

actual class AttachmentFileDownloader(
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
) {
    actual fun saveFile(message: Message) {
        if (message.isAttachmentAvailable) {
            // TODO: Save file...
            val originalMessageMessageMedia = message.messageMedia

            //Getting message media from purchase accept item if is paid.
            //LocalFile and mediaType should be returned from original message
            val mediaUrlAndMessageMedia = message.retrieveImageUrlAndMessageMedia() ?: message.retrieveVideoUrlAndMessageMedia()

//            mediaUrlAndMessageMedia?.second?.let { messageMedia ->
//                // TODO: Save file to downloads...
//                val inputStream: InputStream? = when {
//                    (originalMessageMessageMedia?.localFile != null) -> {
//                        FileInputStream(originalMessageMessageMedia.localFile!!.toFile())
//                    }
//                    else -> {
//                        messageMedia.retrieveRemoteMediaInputStream(
//                            mediaUrlAndMessageMedia.first,
//                            memeServerTokenHandler,
//                            memeInputStreamHandler
//                        )
//                    }
//                }
//
//                try {
//                    inputStream?.use { nnInputStream ->
//                        app.contentResolver.openOutputStream(savedFileUri).use { savedFileOutputStream ->
//                            if (savedFileOutputStream != null) {
//                                nnInputStream.copyTo(savedFileOutputStream, 1024)
//
//                                submitSideEffect(
//                                    ChatSideEffect.Notify(app.getString(R.string.saved_attachment_successfully))
//                                )
//                                return@launch
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    LOG.e(TAG, "Failed to store file: ", e)
//                }
//
//
//            }
        }
    }

}

actual fun createAttachmentFileDownload(
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
): AttachmentFileDownloader = AttachmentFileDownloader(
    memeServerTokenHandler,
    memeInputStreamHandler
)