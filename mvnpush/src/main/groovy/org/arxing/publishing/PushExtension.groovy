package org.arxing.publishing

class PushExtension {
    public String POM_NAME
    public String POM_ARTIFACT_ID
    public String POM_PACKAGING
    public String VERSION_NAME
    public String VERSION_CODE
    public String GROUP
    public String POM_DESCRIPTION
    public String POM_URL
    public String POM_SCM_URL
    public String POM_SCM_CONNECTION
    public String POM_SCM_DEV_CONNECTION
    public String POM_LICENCE_NAME
    public String POM_LICENCE_URL
    public String POM_LICENCE_DIST
    public String POM_DEVELOPER_ID
    public String POM_DEVELOPER_NAME
    public String REPOSITORY_URL
    public String REPOSITORY_URL_RELATIVE

    public String getRealRepoUrl(String rootPath) {
        if (!REPOSITORY_URL.isEmpty())
            return REPOSITORY_URL
        else if (!REPOSITORY_URL_RELATIVE.isEmpty()) {
            return new File(rootPath, REPOSITORY_URL_RELATIVE).toString()
        } else
            throw new Error("Invalid url.")
    }
}
