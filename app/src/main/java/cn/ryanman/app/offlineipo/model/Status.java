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

    public Status floor(){
        switch (this){
            case NOTICE:
                return NOTICE;
            case INQUIRY:
            case ROADSHOW:
            case ANNOUNCE:
                return INQUIRY;
            case OFFLINE:
            case ONLINE:
            case SUCCESS_RATE:
            case SUCCESS_RESULT:
                return OFFLINE;
            case PAYMENT:
                return PAYMENT;
            case LISTED:
                return LISTED;
            default:
                return NONE;
        }
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
