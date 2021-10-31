package com.xhhold.plugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.xhhold.plugin.MediaService
import java.awt.Image
import java.awt.Toolkit
import java.net.URL
import javax.swing.ImageIcon

class MediaArtAction : AnAction() {

    private var artUrl: String? = null

    override fun actionPerformed(e: AnActionEvent) {

    }

    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val metadata = service<MediaService>().selectedPlayer?.playerProperties?.metadata
        val artUrl = metadata?.artUrl
        val album = metadata?.album
        presentation.text = album
        if (this.artUrl == artUrl) {
            super.update(e)
            return
        }
        this.artUrl = artUrl
        if (artUrl == null) {
            presentation.icon = AllIcons.Ide.LocalScopeAction
            super.update(e)
            return
        }
        runCatching {
            val image = Toolkit.getDefaultToolkit().getImage(URL(artUrl))
            presentation.icon = ImageIcon(image.getScaledInstance(24, 24, Image.SCALE_FAST))
        }.onFailure {
            thisLogger().error(it)
        }
        super.update(e)
    }
}