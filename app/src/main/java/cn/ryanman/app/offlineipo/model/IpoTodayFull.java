package cn.ryanman.app.offlineipo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan on 2016/11/26.
 */

public class IpoTodayFull {
    private String event;
    private IpoItem ipo;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public IpoItem getIpo() {
        return ipo;
    }

    public void setIpo(IpoItem ipo) {
        this.ipo = ipo;
    }
}