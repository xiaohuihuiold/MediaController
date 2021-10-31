package com.xhhold.plugin.player

import com.xhhold.plugin.entity.PlayerProperties

abstract class MediaPlayer(var playerProperties: PlayerProperties) {
    fun play() {}
    fun pause() {}
    fun previous() {}
    fun next() {}
}