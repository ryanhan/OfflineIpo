package cn.ryanman.app.offlineipo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ryanman.app.offlineipo.model.IpoItem;

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
        } catch (Exception e) {
            return false;
        }

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

    public static boolean writeToFile(String content, String fileName) {
        File directory = new File(Environment.getExternalStorageDirectory(), "OfflineIpo");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory, fileName);
        try {
            if (file.exists()) {
                file.delete();
            }
            FileWriter fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String readFromFile(String fileName) {
        File directory = new File(Environment.getExternalStorageDirectory(), "OfflineIpo");
        File file = new File(directory, fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            FileReader fd = new FileReader(file);
            char[] chs = new char[1024];
            StringBuilder sb = new StringBuilder();
            while (fd.read(chs) != -1) {
                sb.append(chs);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}