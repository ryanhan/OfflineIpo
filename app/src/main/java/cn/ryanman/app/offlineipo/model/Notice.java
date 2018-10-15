package cn.ryanman.app.offlineipo.model;

/**
 * Created by hanyan on 11/21/2016.
 */

public class Notice {

    private String title;
    private String url;
    private String date;

    public Notice(String title, String url, String date) {
        this.title = title;
        this.url = url;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
