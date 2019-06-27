package org.arxing.publishing

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UploadMavenTask extends DefaultTask {

    UploadMavenTask() {
        group = 'axcore-push'
        dependsOn PublishToMavenTask
    }

    @TaskAction
    void action() {
        PushExtension ext = project.push
        String groupSeg = ext.GROUP.replace(".", "/")
        String msg = "update ${ext.POM_ARTIFACT_ID} to ${ext.VERSION_NAME}"
        Command comm = new Command()
        comm.cd(project.rootDir.toString())
                .exec("git", "add", "mvn-repositories/${groupSeg}/${ext.POM_ARTIFACT_ID}")
                .exec("git", "commit", "-m", "${msg}")
                .exec("git", "push", "origin", "master")
    }
}
