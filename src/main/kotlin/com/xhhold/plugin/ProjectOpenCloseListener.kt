package com.xhhold.plugin

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class ProjectOpenCloseListener : ProjectManagerListener {
    override fun projectOpened(project: Project) {
        service<MediaService>().projectOpened()
    }

    override fun projectClosed(project: Project) {
        service<MediaService>().projectClosed()
    }
}