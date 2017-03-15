package cn.ryanman.app.offlineipo.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyan on 11/24/2016.
 */

public class IpoToday {

    private Status event;
    private String ipoName;

    public Status getEvent() {
        return event;
    }

    public void setEvent(Status event) {
        this.event = event;
    }

    public String getIpoName() {
        return ipoName;
    }

    public void setIpoName(String ipoName) {
        this.ipoName = ipoName;
    }
}
