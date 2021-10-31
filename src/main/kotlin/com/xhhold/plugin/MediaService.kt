package com.xhhold.plugin

import com.intellij.ide.ActivityTracker
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.util.SystemInfo
import com.xhhold.plugin.player.MediaPlayer
import com.xhhold.plugin.service.*

class MediaService {
    private var projectOpened = 0

    private val connected: Boolean
        get() = playerService?.connected == true

    var players: MutableList<MediaPlayer> = mutableListOf()
        private set

    var selectedPlayer: MediaPlayer? = null
        private set

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
        if (selectedPlayer?.playerProperties?.id != player.playerProperties.id) {
            return
        }
        selectedPlayer = player
        ActivityTracker.getInstance().inc()
    }

    private fun onPlayersChanged(players: MutableList<MediaPlayer>) {
        if (players.isEmpty()) {
            selectedPlayer = null
        } else if (selectedPlayer == null) {
            selectedPlayer = players.first()
        }
        this.players = players
        ActivityTracker.getInstance().inc()
    }

    fun selectPlayer(id: String) {
        selectedPlayer = players.find { it.playerProperties.id == id }
    }

    fun play() {
        selectedPlayer?.play()
    }

    fun pause() {
        selectedPlayer?.pause()
    }

    fun previous() {
        selectedPlayer?.previous()
    }

    fun next() {
        selectedPlayer?.next()
    }

}