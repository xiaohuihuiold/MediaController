package com.xhhold.plugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ComboBoxAction
import com.intellij.openapi.components.service
import com.xhhold.plugin.MediaService
import javax.swing.JComponent

class MediaPlayerSelectorAction : ComboBoxAction() {

    private val actions = mutableListOf<AnAction>()

    override fun createPopupActionGroup(button: JComponent?): DefaultActionGroup {
        return DefaultActionGroup(actions)
    }

    override fun update(e: AnActionEvent) {
        val players = service<MediaService>().players
        val selectedPlayer = service<MediaService>().selectedPlayer
        val title = selectedPlayer?.playerProperties?.metadata?.title ?: "[none]"
        e.presentation.text = title.ifEmpty { "[none]" }
        actions.clear()
        actions.addAll(players.map { SelectionAction(it.playerProperties.id, it.playerProperties.name) })
        super.update(e)
    }

    class SelectionAction(private val id: String, private val name: String) : AnAction() {
        override fun actionPerformed(e: AnActionEvent) {
            service<MediaService>().selectPlayer(id)
        }

        override fun update(e: AnActionEvent) {
            val presentation = e.presentation
            val selectedId = service<MediaService>().selectedPlayer?.playerProperties?.id
            presentation.text = name
            if (selectedId == id) {
                presentation.icon = AllIcons.Actions.Commit
            } else {
                presentation.icon = null
            }
            super.update(e)
        }
    }
}