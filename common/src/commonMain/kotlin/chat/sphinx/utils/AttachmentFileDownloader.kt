package chat.sphinx.utils

import chat.sphinx.concepts.meme_input_stream.MemeInputStreamHandler
import chat.sphinx.concepts.meme_server.MemeServerTokenHandler
import chat.sphinx.wrapper.message.Message

expect class AttachmentFileDownloader {
    fun saveFile(message: Message)
}

expect fun createAttachmentFileDownload(
    memeServerTokenHandler: MemeServerTokenHandler,
    memeInputStreamHandler: MemeInputStreamHandler
): AttachmentFileDownloader