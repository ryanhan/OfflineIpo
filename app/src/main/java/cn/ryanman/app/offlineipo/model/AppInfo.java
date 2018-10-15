package cn.ryanman.app.offlineipo.model;

/**
 * Created by hanyan on 11/21/2016.
 */

public class AppInfo {

    private String version;
    private String versionShort;
    private String url;
    private String changelog;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

}
