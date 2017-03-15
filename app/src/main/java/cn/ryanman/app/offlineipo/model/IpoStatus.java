package cn.ryanman.app.offlineipo.model;

/**
 * Created by ryanh on 2016/12/17.
 */

public class IpoStatus {

    private Status current;
    private Status next;
    private String nextDate;

    public Status getCurrent() {
        return current;
    }

    public void setCurrent(Status current) {
        this.current = current;
    }

    public Status getNext() {
        return next;
    }

    public void setNext(Status next) {
        this.next = next;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

}
