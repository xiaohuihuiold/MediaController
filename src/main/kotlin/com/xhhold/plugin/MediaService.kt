package com.xhhold.plugin

import com.intellij.openapi.util.SystemInfo
import com.xhhold.plugin.player.MediaPlayer
import com.xhhold.plugin.service.*

class MediaService {
    private var projectOpened = 0

    val connected: Boolean
        get() = playerService?.connected == true

    private val playerService: MediaPlayerService? by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        var service: MediaPlayerService? = null
        if (SystemInfo.isLinux) {
            service = LinuxMediaPlayerService()
        }
        service?.setOnConnected { onConnected() }
        service?.setOnDisconnected { onDisconnected() }
        service?.setOnPlayerChanged { onPlayerChanged(it) }
        service?.setOnPlayersChanged { onPlayersChanged(it) }
        return@lazy service
    }

    fun projectOpened() = synchronized(this) {
        projectOpened++
        if (projectOpened == 1 && !connected) {
            playerService?.connect()
        }
    }

    fun projectClosed() = synchronized(this) {
        projectOpened--
        if (projectOpened <= 0 && connected) {
            playerService?.disconnect()
        }
    }

    private fun onConnected() {

    }

    private fun onDisconnected() {

    }

    private fun onPlayerChanged(player: MediaPlayer) {

    }

    private fun onPlayersChanged(players: MutableList<MediaPlayer>) {

    }

}