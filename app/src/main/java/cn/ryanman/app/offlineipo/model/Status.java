package cn.ryanman.app.offlineipo.model;

/**
 * Created by Ryan on 2017/3/14.
 */

public enum Status {

    NOTICE, INQUIRY, ROADSHOW, ANNOUNCE, OFFLINE, ONLINE, SUCCESS_RATE, SUCCESS_RESULT, PAYMENT, LISTED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public String next() {
        switch (this) {
            case NOTICE:
                return INQUIRY.toString();

            case INQUIRY:
                return OFFLINE.toString();

            case OFFLINE:
                return PAYMENT.toString();

            case PAYMENT:
                return LISTED.toString();

            default:
                return null;
        }
    }

}
