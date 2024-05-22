package com.matin.happychat.mediaplayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HappyChatMediaPlayer @Inject constructor(@ApplicationContext context: Context) {
    private var player: ExoPlayer = ExoPlayer.Builder(context).build()
    private var playWhenReady = true
    private var mediaItemIndex = 0
    private var startPosition = 0L
    private var listener: Player.Listener? = null
    var currentPosition = 0L
        get() = player.currentPosition
        private set

    fun startPlayer(path: String, newPlaybackListener: Player.Listener) {
        val mediaItem = MediaItem.fromUri(path)
        if (listener != null) {
            player.stop()
            player.removeListener(listener!!)
        }
        listener = newPlaybackListener
        player.addListener(listener!!)
        player.setMediaItems(listOf(mediaItem), mediaItemIndex, startPosition)
        player.seekTo(startPosition)
        player.playWhenReady = playWhenReady
        player.prepare()
    }

    fun pause() {
        player.pause()
    }

    fun play() {
        player.play()
    }

    fun isPaused(path: String): Boolean {
        if (isTheSameSource(path).not()) return false
        return !player.isPlaying && player.playbackState == Player.STATE_READY
    }

    private fun isTheSameSource(path: String): Boolean {
        val mediaItem = player.currentMediaItem
        return mediaItem?.localConfiguration?.uri.toString() == path
    }
}