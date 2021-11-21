package com.xhhold.plugin.player

import com.xhhold.plugin.WinRTMediaSessionManager
import com.xhhold.plugin.entity.PlayerProperties

class WindowsMediaPlayer(
    playerProperties: PlayerProperties
) :
    MediaPlayer(playerProperties) {

    override fun play() {
        WinRTMediaSessionManager.play(playerProperties.id)
    }

    override fun pause() {
        WinRTMediaSessionManager.pause(playerProperties.id)
    }

    override fun skipToPrevious() {
        WinRTMediaSessionManager.skipToPrevious(playerProperties.id)
    }

    override fun skipToNext() {
        WinRTMediaSessionManager.skipToNext(playerProperties.id)
    }
}