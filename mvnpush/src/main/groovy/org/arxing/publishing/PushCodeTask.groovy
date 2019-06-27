package org.arxing.publishing

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PushCodeTask extends DefaultTask {

    PushCodeTask() {
        group = 'axcore-push'
        dependsOn 'commitCode'
    }

    @TaskAction
    void action() {
        Command comm = new Command()
        comm.cd(project.rootDir.toString()).exec("git", "push", "origin", "master")
    }
}
