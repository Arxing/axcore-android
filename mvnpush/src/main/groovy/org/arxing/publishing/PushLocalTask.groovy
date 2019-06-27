package org.arxing.publishing

import org.gradle.api.DefaultTask

class PushLocalTask extends DefaultTask {

    PushLocalTask() {
        group = 'axcore-android-push'
        dependsOn 'uploadArchives'
    }
}
