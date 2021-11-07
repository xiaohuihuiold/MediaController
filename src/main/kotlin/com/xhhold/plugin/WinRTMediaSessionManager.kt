package com.xhhold.plugin

import com.intellij.openapi.util.SystemInfo
import com.xhhold.plugin.service.ValueChanged

object WinRTMediaSessionManager {

    private var onSessionsChanged: ValueChanged<Array<String>>? = null
    private var onSessionChanged: ValueChanged<MediaSession>? = null
    private val sessions = mutableMapOf<String, MediaSession?>()

    init {
        if (SystemInfo.isWindows) {
            System.load("F:\\idea\\MediaController\\winrt_media_session_manager\\build\\Debug\\winrt_media_session_manager.dll")
        }
    }

    external fun refreshSessions()

    external fun refreshSession(id: String)

    fun listenSessions(onSessionsChanged: ValueChanged<Array<String>>?) {
        this.onSessionsChanged = onSessionsChanged
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
        if (!sessions.containsKey(id)) {
            return
        }
        val session = MediaSession(id, title, artist, album, playStatus)
        onSessionChanged?.invoke(session)
    }

    class MediaSession(id: String, title: String, artist: String, album: String, playStatus: Int) {
    }
}