package org.arxing.publishing


import org.gradle.api.Plugin
import org.gradle.api.Project

class PushPlugin implements Plugin<Project> {
    public static boolean showProperties

    static boolean showProperties(Project project) {
        return project.extensions.hasProperty('SHOW_PROPERTIES') && project.extensions.properties['SHOW_PROPERTIES']
    }

    @Override
    void apply(Project project) {
        PushExtension extension = project.extensions.create('push', PushExtension)
        project.apply from: "${new File(project.rootDir, "mvn-push.gradle").toString()}"
        project.afterEvaluate {
            showProperties = showProperties(project)
            if (showProperties)
                printExt(project.push)
            buildProperties(project, extension)

            project.task([type: PublishToMavenTask], 'publishToMaven')
            project.task([type: SyncModuleCodeTask], 'syncCode')
        }
    }

    private void printExt(PushExtension extension) {
        println "Ready to publish maven..."
        println "\t\tPOM_NAME : ${extension.POM_NAME}"
        println "\t\tPOM_ARTIFACT_ID: ${extension.POM_ARTIFACT_ID}"
        println "\t\tPOM_PACKAGING: ${extension.POM_PACKAGING}"
        println "\t\tVERSION_NAME: ${extension.VERSION_NAME}"
        println "\t\tVERSION_CODE: ${extension.VERSION_CODE}"
        println "\t\tGROUP: ${extension.GROUP}"
        println "\t\tPOM_DESCRIPTION: ${extension.POM_DESCRIPTION}"
        println "\t\tPOM_URL: ${extension.POM_URL}"
        println "\t\tPOM_SCM_URL: ${extension.POM_SCM_URL}"
        println "\t\tPOM_SCM_CONNECTION: ${extension.POM_SCM_CONNECTION}"
        println "\t\tPOM_SCM_DEV_CONNECTION: ${extension.POM_SCM_DEV_CONNECTION}"
        println "\t\tPOM_LICENCE_NAME: ${extension.POM_LICENCE_NAME}"
        println "\t\tPOM_LICENCE_URL: ${extension.POM_LICENCE_URL}"
        println "\t\tPOM_LICENCE_DIST: ${extension.POM_LICENCE_DIST}"
        println "\t\tPOM_DEVELOPER_ID: ${extension.POM_DEVELOPER_ID}"
        println "\t\tPOM_DEVELOPER_NAME: ${extension.POM_DEVELOPER_NAME}"
        println "\t\tREPOSITORY_URL: ${extension.REPOSITORY_URL}"
        println "\t\tREPOSITORY_URL_RELATIVE: ${extension.REPOSITORY_URL_RELATIVE}"
    }

    private void buildProperties(Project project, PushExtension extension) {
        PropertiesHelper helper = new PropertiesHelper("gradle")
        helper.addProperty("POM_NAME", extension.POM_NAME)
        helper.addProperty("POM_ARTIFACT_ID", extension.POM_ARTIFACT_ID)
        helper.addProperty("POM_PACKAGING", extension.POM_PACKAGING)
        helper.addProperty("VERSION_NAME", extension.VERSION_NAME)
        helper.addProperty("VERSION_CODE", extension.VERSION_CODE)
        helper.addProperty("GROUP", extension.GROUP)
        helper.addProperty("POM_DESCRIPTION", extension.POM_DESCRIPTION)
        helper.addProperty("POM_URL", extension.POM_URL)
        helper.addProperty("POM_SCM_URL", extension.POM_SCM_URL)
        helper.addProperty("POM_SCM_CONNECTION", extension.POM_SCM_CONNECTION)
        helper.addProperty("POM_SCM_DEV_CONNECTION", extension.POM_SCM_DEV_CONNECTION)
        helper.addProperty("POM_LICENCE_DIST", extension.POM_LICENCE_DIST)
        helper.addProperty("POM_LICENCE_URL", extension.POM_LICENCE_URL)
        helper.addProperty("POM_LICENCE_NAME", extension.POM_LICENCE_NAME)
        helper.addProperty("POM_DEVELOPER_ID", extension.POM_DEVELOPER_ID)
        helper.addProperty("POM_DEVELOPER_NAME", extension.POM_DEVELOPER_NAME)
        helper.addProperty("REPOSITORY_URL", extension.REPOSITORY_URL)
        helper.addProperty("REPOSITORY_URL_RELATIVE", extension.REPOSITORY_URL_RELATIVE)
        String folder = project.projectDir.toString()
        helper.save(folder)
    }
}
