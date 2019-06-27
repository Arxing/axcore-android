package org.arxing.publishing

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UploadMavenTask extends DefaultTask {
    PushExtension ext

    UploadMavenTask() {
        group = 'axcore-push'
        dependsOn 'pushCode', 'publishToMaven'
        ext = project.push
    }

    @TaskAction
    void action() {
        if (checkExists()) {
            println "此版號已存在 請更新版號"
            return
        }
        String groupSeg = ext.GROUP.replace(".", "/")
        String msg = "update ${ext.POM_ARTIFACT_ID} to ${ext.VERSION_NAME}"
        Command comm = new Command()
        comm.cd(project.rootDir.toString())
                .exec("git", "add", "mvn-repositories/${groupSeg}/${ext.POM_ARTIFACT_ID}")
                .exec("git", "commit", "-m", "${msg}")
                .exec("git", "push", "origin", "master")
    }

    boolean checkExists() {
        String groupSeg = ext.GROUP.replace(".", "/")
        return new File(project.rootDir, "repositories/${groupSeg}/${ext.POM_ARTIFACT_ID}/${ext.VERSION_NAME}").exists()
    }
}
