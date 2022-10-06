package chat.sphinx.common.components

import chat.sphinx.common.models.ChatMessage
import chat.sphinx.di.container.SphinxContainer
import com.soywiz.klock.TimeSpan
import com.soywiz.korau.sound.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*


//TODO: fix the null-unsafe calls
class AudioPlayer {

    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers

    private var loadedSounds : HashMap<String, Sound> = HashMap()
    private var channel: SoundChannel? = null

    fun loadAudio(chatMessage: ChatMessage) {
        loadedSounds["${chatMessage.message.id.value}"]?.let { sound ->

            chatMessage.audioLayoutState.value?.let { audioState ->
                if (audioState.isPlaying) {
                    return@let
                }
            }

            chatMessage.audioLayoutState.value = ChatMessage.AudioLayoutState(
                length = sound.length.seconds.toInt(),
                currentTime = chatMessage.audioLayoutState.value?.currentTime ?: 0,
                isPlaying = chatMessage.audioLayoutState.value?.isPlaying ?: false
            )
            return@let
        }

        chatMessage?.message?.messageMedia?.localFile?.toString()?.let {
            if (it.contains(".wav")) {
                scope.launch(dispatchers.mainImmediate) {
                    val file = applicationVfs[it].readSound()
                    loadedSounds["${chatMessage.message.id.value}"] = file

                    chatMessage.audioLayoutState.value = ChatMessage.AudioLayoutState(
                        length = file.length.seconds.toInt(),
                        currentTime = 0,
                        isPlaying = false
                    )
                }
            }
        }
    }

    fun playAudio(chatMessage: ChatMessage) {
        val messageAudioState = chatMessage.audioLayoutState.value

        messageAudioState?.let {
            if (it.isPlaying) {
                channel?.pause()

                chatMessage.audioLayoutState.value = ChatMessage.AudioLayoutState(
                    length = it.length,
                    currentTime = it.currentTime,
                    isPlaying = false
                )
                return
            }
        } ?: run {
            channel?.pause()
        }

        scope.launch(dispatchers.mainImmediate) {
            loadedSounds["${chatMessage.message.id.value}"]?.let {
                it.playAndWait(
                    startTime = TimeSpan((chatMessage.audioLayoutState.value?.currentTime ?: 0 * 1000).toDouble()),
                    progress = { current, total ->
                        channel = this

                        scope.launch(dispatchers.mainImmediate) {

                            chatMessage.audioLayoutState.value = ChatMessage.AudioLayoutState(
                                length = total.seconds.toInt(),
                                currentTime = current.seconds.toInt(),
                                isPlaying = true
                            )

                            println("CURRENT: $current")
                            println("TOTAL: $total")
                        }
                    }
                )
            }
        }
    }
}