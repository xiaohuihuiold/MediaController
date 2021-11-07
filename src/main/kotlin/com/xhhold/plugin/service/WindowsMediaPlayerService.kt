package com.xhhold.plugin.service

import com.intellij.openapi.diagnostic.thisLogger
import com.xhhold.plugin.WinRTMediaSessionManager

class WindowsMediaPlayerService : MediaPlayerService() {
    override fun connect() {
        connected = true
        listenMediaSessions()
    }

    override fun disconnect() {
    }

    private fun listenMediaSessions() = runCatching {
        WinRTMediaSessionManager.listenSessions {
            thisLogger().info(it.toString())
        }
        refreshPlayers()
    }.onFailure {
        thisLogger().error(it)
    }

    override fun refreshPlayers() {
        WinRTMediaSessionManager.refreshSessions()
    }
}