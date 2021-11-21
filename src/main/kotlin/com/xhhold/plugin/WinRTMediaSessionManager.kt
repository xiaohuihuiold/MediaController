package com.xhhold.plugin

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.SystemInfo
import com.xhhold.plugin.service.ValueChanged

object WinRTMediaSessionManager {

    private var onSessionsChanged: ValueChanged<Array<String>>? = null
    private var onSessionChanged: ValueChanged<MediaSession>? = null
    private val sessions = mutableMapOf<String, MediaSession?>()

    init {
        runCatching {
            if (SystemInfo.isWindows) {
                System.load("F:\\idea\\MediaController\\wmsm\\cmake-build-debug\\wmsm.dll")
            }
        }.onFailure {
            thisLogger().error(it)
        }
    }

    external fun refreshSessions()

    external fun refreshSession(id: String)

    external fun play(id: String)

    external fun pause(id: String)

    external fun skipToPrevious(id: String)

    external fun skipToNext(id: String)

    fun listenSessions(onSessionsChanged: ValueChanged<Array<String>>?) {
        this.onSessionsChanged = onSessionsChanged
    }

    fun listenSession(onSessionChanged: ValueChanged<MediaSession>?) {
        this.onSessionChanged = onSessionChanged
    }

    @JvmStatic
    fun onSessions(ids: Array<String>) {
        sessions.clear()
        ids.forEach {
            sessions[it] = null
        }
        onSessionsChanged?.invoke(ids)
    }

    @JvmStatic
    fun onSession(id: String, title: String, artist: String, album: String, playStatus: Int) {
        val session = MediaSession(id, title, artist, album, playStatus)
        onSessionChanged?.invoke(session)
    }

    class MediaSession(
        val id: String,
        val title: String,
        val artist: String,
        val album: String,
        val playStatus: Int
    ) {
    }
}