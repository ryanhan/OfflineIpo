package cn.ryanman.app.offlineipo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoStatus;

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

    public static IpoStatus getNextStep(IpoItem item) throws ParseException{
        Date now = new Date();
        IpoStatus status = new IpoStatus();
        if (now.before(parseDate(item.getInquiryDate()))){
            status.setCurrent(Value.NOTICE);
            status.setNext(Value.INQUIRY);
            status.setNextDate(item.getInquiryDate());
        }
        else if(now.before(parseDate(item.getInquiryEndDate()))){
            status.setCurrent(Value.INQUIRY);
            status.setNext(Value.OFFLINE);
            status.setNextDate(item.getOfflineDate());
        }

        else if(now.before(parseDate(item.getOfflineDate()))){
            status.setCurrent(Value.ANNOUNCE);
            status.setCurrentDate(item.getAnnounceDate());
            status.setNext(Value.OFFLINE);
            status.setNextDate(item.getPaymentDate());
        }

        else if((int)daysAfter(item.getOfflineDate()) == 0){
            status.setCurrent(Value.OFFLINE);
            status.setNext(Value.PAYMENT);
            status.setNextDate(item.getPaymentDate());
        }
        else if(now.before(parseDate(item.getPaymentDate()))){
            status.setCurrent(Value.SUCCESS_RESULT);
            status.setCurrentDate(item.getSuccessResultDate());
            status.setNext(Value.PAYMENT);
            status.setNextDate(item.getPaymentDate());
        }
        else if((int)daysAfter(item.getPaymentDate()) == 0){
            status.setCurrent(Value.PAYMENT);
            status.setNext(Value.LISTED);
            status.setNextDate(item.getListedDate());
        }
        else{
            if (item.getListedDate() == null){
                status.setNext(Value.LISTED);
            }
            else if(now.before(parseDate(item.getListedDate()))){
                status.setNext(Value.LISTED);
                status.setNextDate(item.getListedDate());
            }
            else{
                status.setCurrent(Value.LISTED);
            }
        }

        return status;
    }

}
