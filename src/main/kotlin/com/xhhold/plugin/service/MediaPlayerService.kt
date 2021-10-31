package com.xhhold.plugin.service

import com.xhhold.plugin.player.MediaPlayer

typealias VoidCallback = () -> Unit
typealias ValueChanged<T> = (T) -> Unit
typealias OnConnected = VoidCallback
typealias OnDisconnected = VoidCallback
typealias OnPlayerChanged = ValueChanged<MediaPlayer>
typealias OnPlayersChanged = ValueChanged<MutableList<MediaPlayer>>

abstract class MediaPlayerService {
    var connected: Boolean = false
        protected set

    private var onConnected: OnConnected? = null
    private var onDisconnected: OnDisconnected? = null
    private var onPlayerChanged: OnPlayerChanged? = null
    private var onPlayersChanged: OnPlayersChanged? = null

    fun setOnConnected(onConnected: OnConnected?) {
        this.onConnected = onConnected
    }

    fun setOnDisconnected(onDisconnected: OnDisconnected?) {
        this.onDisconnected = onDisconnected
    }

    fun setOnPlayerChanged(onPlayerChanged: OnPlayerChanged?) {
        this.onPlayerChanged = onPlayerChanged
    }

    fun setOnPlayersChanged(onPlayersChanged: OnPlayersChanged?) {
        this.onPlayersChanged = onPlayersChanged
    }

    abstract fun connect()
    abstract fun disconnect()
    abstract fun refreshPlayers()
}