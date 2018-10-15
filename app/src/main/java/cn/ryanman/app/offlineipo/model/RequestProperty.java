package cn.ryanman.app.offlineipo.model;

/**
 * Created by hanyan on 11/21/2016.
 */

public class RequestProperty {

    private String key;
    private String value;

    public RequestProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
