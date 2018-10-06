package cn.ryanman.app.offlineipo.utils;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;
import cn.ryanman.app.offlineipo.model.Status;

/**
 * Created by ryan on 2016/11/29.
 */

public class AppUtils {

    public static double daysAfter(String date) throws ParseException {
        Date d = parseDate(date);
        Date now = new Date();
        double days = (now.getTime() - d.getTime()) / 1000.0 / 3600 / 24;
        return days;
    }

    public static boolean isToday(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String now = df.format(new Date());
        if (now.equals(date)) {
            return true;
        } else
            return false;
    }

    public static Date parseDate(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(date);
    }

    public static Date parseDateTime(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(date);
    }

    public static boolean isListed(IpoItem item) {
        Date now = new Date();
        try {
            if (now.before(parseDate(item.getListedDate()))) {
                return false;
            } else {
                return true;
            }
        }
        catch (Exception e){
            return false;
        }

    }

    public static IpoStatus getIpoStatus(IpoItem item) throws ParseException {
        Date now = new Date();
        IpoStatus status = new IpoStatus();
        if (Value.ipoTodayMap.containsKey(item.getName())) {
            status.setCurrent(Value.ipoTodayMap.get(item.getName()));
        }

        if (now.before(parseDate(item.getInquiryDate()))) {
            //status.setCurrent(Value.NOTICE);
            status.setNext(Status.INQUIRY);
            status.setNextDate(item.getInquiryDate());
        } else if (now.before(parseDate(item.getOfflineDate()))) {
            status.setNext(Status.OFFLINE);
            status.setNextDate(item.getOfflineDate());
        } else if (now.before(parseDate(item.getPaymentDate()))) {
            status.setNext(Status.PAYMENT);
            status.setNextDate(item.getPaymentDate());
        } else if (isToday(item.getPaymentDate())) {
            status.setNext(Status.LISTED);
        } else if (now.after(parseDate(item.getPaymentDate()))) {
            if (item.getListedDate() == null) {
                status.setNext(Status.LISTED);
            } else {
                if (now.before(parseDate(item.getListedDate()))) {
                    status.setNext(Status.LISTED);
                    status.setNextDate(item.getListedDate());
                } else {
                    status.setCurrent(Status.LISTED);
                }
            }
        }
        return status;
    }

    public static boolean isStatusImportant(cn.ryanman.app.offlineipo.model.Status status) {
        if (status.equals(cn.ryanman.app.offlineipo.model.Status.NOTICE) ||
                status.equals(cn.ryanman.app.offlineipo.model.Status.INQUIRY) ||
                status.equals(cn.ryanman.app.offlineipo.model.Status.OFFLINE) ||
                status.equals(cn.ryanman.app.offlineipo.model.Status.PAYMENT) ||
                status.equals(cn.ryanman.app.offlineipo.model.Status.LISTED)
                ) {
            return true;
        } else {
            return false;
        }
    }

}
