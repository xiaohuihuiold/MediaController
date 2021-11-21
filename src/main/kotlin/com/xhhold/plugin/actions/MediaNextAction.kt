package com.xhhold.plugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.xhhold.plugin.MediaService

class MediaNextAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        service<MediaService>().skipToNext()
    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val properties = service<MediaService>().selectedPlayer?.playerProperties
        presentation.isVisible = properties?.canGoNext != false
        super.update(e)
    }
}