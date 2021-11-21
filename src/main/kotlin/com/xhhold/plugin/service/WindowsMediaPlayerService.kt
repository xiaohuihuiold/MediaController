package com.xhhold.plugin.service

import com.intellij.openapi.diagnostic.thisLogger
import com.xhhold.plugin.WinRTMediaSessionManager
import com.xhhold.plugin.entity.MediaMetadata
import com.xhhold.plugin.entity.MediaPlayState
import com.xhhold.plugin.entity.PlayerProperties
import com.xhhold.plugin.player.WindowsMediaPlayer

class WindowsMediaPlayerService : MediaPlayerService() {

    private val players = HashMap<String, WindowsMediaPlayer>()

    override fun connect() {
        connected = true
        listenMediaSessions()
        listenMediaSession()
        refreshPlayers()
    }

    override fun disconnect() {
    }

    private fun listenMediaSessions() = runCatching {
        WinRTMediaSessionManager.listenSessions {
            thisLogger().info(it.toString())
        }
    }.onFailure {
        thisLogger().error(it)
    }

    private fun listenMediaSession() = runCatching {
        WinRTMediaSessionManager.listenSession {
            val player = WindowsMediaPlayer(
                PlayerProperties(
                    id = it.id,
                    name = it.id,
                    canControl = true,
                    canPlay = true,
                    canPause = true,
                    canGoPrevious = true,
                    canGoNext = true,
                    canSeek = true,
                    playState = if (it.playStatus == 4) MediaPlayState.PLAYING else MediaPlayState.PAUSED,
                    position = 0L,
                    metadata = MediaMetadata(
                        title = it.title,
                        album = it.album,
                        artists = arrayListOf(it.artist)
                    ),
                )
            )
            players[it.id] = player
            onPlayersChanged?.invoke(players.values.toMutableList())
            onPlayerChanged?.invoke(player)
        }
    }.onFailure {
        thisLogger().error(it)
    }

    override fun refreshPlayers() {
        WinRTMediaSessionManager.refreshSessions()
    }
}