package chat.sphinx.common.components

import chat.sphinx.common.models.ChatMessage
import chat.sphinx.di.container.SphinxContainer
import com.soywiz.klock.TimeSpan
import com.soywiz.korau.sound.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*

class AudioPlayer {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    private var loadedSounds : MutableMap<String, Sound> = mutableMapOf()
    private var channel: SoundChannel? = null

    private var playingMessage: ChatMessage? = null

    fun loadAudio(chatMessage: ChatMessage) {
        if (chatMessage.message.id == playingMessage?.message?.id) {
            return
        }

        loadedSounds["${chatMessage.message.id.value}"]?.let { sound ->
            val audioState = chatMessage.audioState

            chatMessage.audioState.value = ChatMessage.AudioState(
                length = sound.length.seconds.toInt(),
                currentTime = audioState.value?.currentTime ?: 0,
                progress = audioState.value?.progress ?: 0.0,
                isPlaying = audioState.value?.isPlaying ?: false
            )
            return
        }

        chatMessage?.message?.messageMedia?.localFile?.toString()?.let {
            scope.launch(dispatchers.mainImmediate) {
                val file = applicationVfs[it].readSound()
                loadedSounds["${chatMessage.message.id.value}"] = file

                chatMessage.audioState.value = ChatMessage.AudioState(
                    length = file.length.seconds.toInt(),
                    currentTime = 0,
                    progress = 0.0,
                    isPlaying = false
                )
            }
        }
    }

    fun playAudio(chatMessage: ChatMessage) {
        playingMessage?.let {

            channel?.pause()

            if (chatMessage.message.id == it.message.id) {
                chatMessage.audioState.value = ChatMessage.AudioState(
                    length = chatMessage.audioState.value?.length ?: 0,
                    currentTime = chatMessage.audioState.value?.currentTime ?: 0,
                    progress = chatMessage.audioState.value?.progress ?: 0.0,
                    isPlaying = false
                )
                playingMessage = null
                return
            } else {
                playingMessage?.audioState?.value?.isPlaying = false
            }
        }

        scope.launch(dispatchers.mainImmediate) {
            loadedSounds["${chatMessage.message.id.value}"]?.let {
                it.playAndWait(
                    times = 1.playbackTimes,
                    startTime = TimeSpan(((chatMessage.audioState.value?.currentTime ?: 0) * 1000).toDouble()),
                    progress = { current, total ->
                        channel = this

                        scope.launch(dispatchers.mainImmediate) {

                             val audioState = ChatMessage.AudioState(
                                length = total.seconds.toInt(),
                                currentTime = if (current < total) current.seconds.toInt() else 0,
                                progress = if (current < total) current.milliseconds / total.milliseconds else 0.0,
                                isPlaying = (current < total)
                            )

                            chatMessage.audioState.value = audioState
                            playingMessage = chatMessage

                            println("CURRENT: ${current.seconds}")
                            println("TOTAL: ${total.seconds}")
                        }
                    }
                )
            }
        }
    }
}