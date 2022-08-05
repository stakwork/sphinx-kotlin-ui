package utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import chat.sphinx.utils.platform.getFileSystem
import chat.sphinx.wrapper.message.media.MediaType


import okio.Path
import theme.*
import java.nio.file.Files

fun Path.deduceMediaType(): MediaType {
  val fileMetadata = getFileSystem().metadata(this)

  return fileMetadata.extra(ContentTypeExtra::class)?.let { contentTypeExtra ->
    when {
      contentTypeExtra.contentType.lowercase().startsWith("image") -> MediaType.Image(contentTypeExtra.contentType)
      contentTypeExtra.contentType.lowercase().startsWith("audio") -> MediaType.Audio(contentTypeExtra.contentType)
      contentTypeExtra.contentType.lowercase().startsWith("video") -> MediaType.Video(contentTypeExtra.contentType)
      else -> MediaType.Unknown("text/unknow")
    }
  } ?: try {
    val contentType = Files.probeContentType(this.toNioPath())

    when {
      contentType.lowercase().startsWith("image") -> MediaType.Image(contentType)
      contentType.lowercase().startsWith("audio") -> MediaType.Audio(contentType)
      contentType.lowercase().startsWith("video") -> MediaType.Video(contentType)
      else -> MediaType.Unknown("text/unknown")
    }
  } catch (e: Exception) {
    MediaType.Unknown("text/unknown")
  }
}


internal data class ContentTypeExtra(
  val contentType: String
)

fun getRandomColorRes(): Color {
    return listOf<Color>(
      randomColor1,
      randomColor2,
      randomColor3,
      randomColor4,
      randomColor5,
      randomColor6,
      randomColor7,
      randomColor8,
      randomColor9,
      randomColor10,
      randomColor11,
      randomColor12,
      randomColor13,
      randomColor14,
      randomColor15,
      randomColor16,
      randomColor17,
      randomColor18,
      randomColor19,
      randomColor20,
    ).shuffled()[0]
}

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
  return if (condition) {
    modifier.invoke(this)
  } else {
    this
  }
}
