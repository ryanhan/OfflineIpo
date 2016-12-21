package cn.ryanman.app.offlineipo.model;

/**
 * Created by ryanh on 2016/12/17.
 */

public class IpoStatus {

    private String current;
    private String next;
    private String nextDate;

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

}
