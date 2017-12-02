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

    public static Date parseDate(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(date);
    }

    public static Date parseDateTime(String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.parse(date);
    }

    public static IpoStatus getIpoStatus(IpoItem item) throws ParseException{
        Date now = new Date();
        IpoStatus status = new IpoStatus();
        if (Value.ipoTodayMap.containsKey(item.getName())){
            status.setCurrent(Value.ipoTodayMap.get(item.getName()));
        }

        if (now.before(parseDate(item.getInquiryDate()))){
            //status.setCurrent(Value.NOTICE);
            status.setNext(Status.INQUIRY);
            status.setNextDate(item.getInquiryDate());
        }

        else if(now.before(parseDate(item.getOfflineDate()))){
            status.setNext(Status.OFFLINE);
            status.setNextDate(item.getOfflineDate());
        }

        else if(now.before(parseDate(item.getPaymentDate()))){
            status.setNext(Status.PAYMENT);
            status.setNextDate(item.getPaymentDate());
        }
        else if((int)daysAfter(item.getPaymentDate()) == 0){
            status.setNext(Status.LISTED);
        }
        else if(now.after(parseDate(item.getPaymentDate()))){
            if (item.getListedDate() == null){
                status.setNext(Status.LISTED);
            }
            else{
                if (now.before(parseDate(item.getListedDate()))){
                    status.setNext(Status.LISTED);
                    status.setNextDate(item.getListedDate());
                }
                else {
                    status.setCurrent(Status.LISTED);
                }
            }
        }
        return status;
    }

}
