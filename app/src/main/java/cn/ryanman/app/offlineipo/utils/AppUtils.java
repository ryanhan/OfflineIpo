package cn.ryanman.app.offlineipo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
