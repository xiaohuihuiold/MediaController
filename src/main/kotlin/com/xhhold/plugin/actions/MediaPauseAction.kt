package com.xhhold.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.xhhold.plugin.MediaService
import com.xhhold.plugin.entity.MediaPlayState

class MediaPauseAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        service<MediaService>().pause()
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val properties = service<MediaService>().selectedPlayer?.playerProperties
        presentation.isVisible = properties?.playState == MediaPlayState.PLAYING && properties.canPause != false
        super.update(e)
    }
}