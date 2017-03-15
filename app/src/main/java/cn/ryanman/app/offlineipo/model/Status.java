package cn.ryanman.app.offlineipo.model;

/**
 * Created by Ryan on 2017/3/14.
 */

public enum Status {

    NONE, NOTICE, INQUIRY, ROADSHOW, ANNOUNCE, OFFLINE, ONLINE, SUCCESS_RATE, SUCCESS_RESULT, PAYMENT, LISTED;

    @Override
    public String toString() {
        return super.toString();
    }

    public Status next() {
        switch (this) {
            case NOTICE:
                return INQUIRY;

            case INQUIRY:
                return OFFLINE;

            case OFFLINE:
                return PAYMENT;

            case PAYMENT:
                return LISTED;

            default:
                return null;
        }
    }


}
