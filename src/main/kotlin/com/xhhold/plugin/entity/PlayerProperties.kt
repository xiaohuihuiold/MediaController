package com.xhhold.plugin.entity

enum class MediaPlayState {
    PLAYING,
    PAUSED,
}

data class PlayerProperties(
    val id: Int,
    val name: String,
    var canControl: Boolean,
    var canPlay: Boolean,
    var canPause: Boolean,
    var canGoPrevious: Boolean,
    var canGoNext: Boolean,
    var canSeek: Boolean,
    var playState: MediaPlayState,
    var metadata: MediaMetadata?,
)
