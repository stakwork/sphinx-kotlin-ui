package chat.sphinx.utils

import chat.sphinx.wrapper.message.media.FileName
import okio.Path

expect suspend fun saveFile(fileName: FileName, path: Path)