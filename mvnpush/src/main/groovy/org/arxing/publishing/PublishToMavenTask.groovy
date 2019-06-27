package org.arxing.publishing

import org.gradle.api.DefaultTask

class PublishToMavenTask extends DefaultTask {

    PublishToMavenTask() {
        group = 'axcore-push'
        dependsOn 'uploadArchives'
    }
}
