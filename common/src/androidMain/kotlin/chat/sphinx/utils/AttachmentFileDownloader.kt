package chat.sphinx.utils

import chat.sphinx.concepts.meme_input_stream.MemeInputStreamHandler
import chat.sphinx.concepts.meme_server.MemeServerTokenHandler
import chat.sphinx.wrapper.message.Message

actual class AttachmentFileDownloader(
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
) {
    actual fun saveFile(message: Message) {
        // TODO: Save file for android
    }
}

actual fun createAttachmentFileDownload(
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
): AttachmentFileDownloader = AttachmentFileDownloader(
    memeServerTokenHandler,
    memeInputStreamHandler
)