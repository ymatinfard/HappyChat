package com.matin.happychat.mediaplayer

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceMessagePlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playbackListener: Player.Listener? = null
    private var currentVoiceUri: Uri? = null

    val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    val currentPosition: Long
        get() = exoPlayer.currentPosition

    val duration: Long
        get() = exoPlayer.duration

    /**
     * Starts playback of the given voice message file.
     * Replaces any existing media item and listener.
     */
    fun play(uri: Uri, listener: Player.Listener) {
        if (isSameVoice(uri)) {
            // Resume playback if already set up
            exoPlayer.play()
            return
        }

        stop()

        currentVoiceUri = uri
        val mediaItem = MediaItem.fromUri(uri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = true

        playbackListener = listener
        exoPlayer.addListener(listener)

        exoPlayer.prepare()
    }

    /**
     * Pauses the current playback.
     */
    fun pause() {
        exoPlayer.pause()
    }

    /**
     * Stops playback and clears current state.
     */
    fun stop() {
        exoPlayer.stop()
        currentVoiceUri = null
        removePlaybackListener()
    }

    /**
     * Releases all resources held by the player.
     * Call from appropriate lifecycle (e.g., ViewModel.onCleared).
     */
    fun release() {
        stop()
        exoPlayer.release()
    }

    /**
     * Returns true if the given voice URI is currently loaded.
     */
    fun isPlayingVoice(uri: Uri): Boolean {
        return isSameVoice(uri) && exoPlayer.isPlaying
    }

    /**
     * Returns true if the given URI is the same as the currently loaded one.
     */
    private fun isSameVoice(uri: Uri): Boolean {
        return currentVoiceUri?.toString() == uri.toString()
    }

    private fun removePlaybackListener() {
        playbackListener?.let { exoPlayer.removeListener(it) }
        playbackListener = null
    }
}
