package org.arxing.publishing

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CommitCodeTask extends DefaultTask {

    CommitCodeTask() {
        group = 'axcore-push'
    }

    @TaskAction
    void action() {
        String msg = "[ ${project.name} ]: ${project.push.COMMIT_MESSAGE}"
        Command comm = new Command()
        comm.cd(project.rootDir.toString()).exec("git", "add", "${project.name}")
                .exec("git", "commit", "-m", "${msg}")
    }
}
