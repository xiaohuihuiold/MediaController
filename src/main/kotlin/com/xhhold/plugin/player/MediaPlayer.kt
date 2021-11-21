package com.xhhold.plugin.player

import com.xhhold.plugin.entity.PlayerProperties

abstract class MediaPlayer(var playerProperties: PlayerProperties) {
    open fun play() {}
    open fun pause() {}
    open fun skipToPrevious() {}
    open fun skipToNext() {}
}