package com.xhhold.plugin.entity

data class MediaMetadata(
    val title: String? = null,
    val album: String? = null,
    val artUrl: String? = null,
    val artists: MutableList<String>? = null,
    val duration: Long? = null,
    val updateTime: Long? = null,
)
