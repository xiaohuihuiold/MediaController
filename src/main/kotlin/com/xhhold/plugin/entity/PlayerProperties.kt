package com.xhhold.plugin.entity

enum class MediaPlayState {
    PLAYING,
    PAUSED,
}

data class PlayerProperties(
    val id: String,
    val name: String,
    var canControl: Boolean = false,
    var canPlay: Boolean = false,
    var canPause: Boolean = false,
    var canGoPrevious: Boolean = false,
    var canGoNext: Boolean = false,
    var canSeek: Boolean = false,
    var playState: MediaPlayState = MediaPlayState.PAUSED,
    var position: Long? = 0L,
    var metadata: MediaMetadata? = null,
)
