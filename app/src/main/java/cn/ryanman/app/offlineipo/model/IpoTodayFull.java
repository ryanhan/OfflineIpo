package cn.ryanman.app.offlineipo.model;

/**
 * Created by ryan on 2016/11/26.
 */

public class IpoTodayFull {
    private Status event;
    private IpoItem ipo;

    public Status getEvent() {
        return event;
    }

    public void setEvent(Status event) {
        this.event = event;
    }

    public IpoItem getIpo() {
        return ipo;
    }

    public void setIpo(IpoItem ipo) {
        this.ipo = ipo;
    }
}