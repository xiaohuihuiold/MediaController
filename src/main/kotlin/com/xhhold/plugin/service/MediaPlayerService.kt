package com.xhhold.plugin.service

import com.xhhold.plugin.player.MediaPlayer

typealias VoidCallback = () -> Unit
typealias ValueChanged<T> = (T) -> Unit
typealias OnConnected = VoidCallback
typealias OnDisconnected = VoidCallback
typealias OnPlayerChanged = ValueChanged<MediaPlayer>
typealias OnPlayersChanged = ValueChanged<MutableList<MediaPlayer>>

abstract class MediaPlayerService {
    @Volatile
    var connected: Boolean = false
        protected set

    protected var onConnected: OnConnected? = null
        private set
    protected var onDisconnected: OnDisconnected? = null
        private set
    protected var onPlayerChanged: OnPlayerChanged? = null
        private set
    protected var onPlayersChanged: OnPlayersChanged? = null
        private set

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