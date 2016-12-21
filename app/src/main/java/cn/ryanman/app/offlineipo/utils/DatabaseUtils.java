package cn.ryanman.app.offlineipo.utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoToday;
import cn.ryanman.app.offlineipo.model.IpoTodayFull;


public class DatabaseUtils {

    public static void createDatabase(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        dbHelper.close();
    }

    public static void insertIpoTodayList(Context context, List<IpoToday> ipoTodayList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        sqliteDatabase.delete(DatabaseHelper.IPO_TODAY, null, null);

        for (int i = 0; i < ipoTodayList.size(); i++) {
            ContentValues values = createIpoTodayValues(ipoTodayList.get(i).getEvent(), ipoTodayList.get(i).getIpoName());
            sqliteDatabase.insert(DatabaseHelper.IPO_TODAY, null, values);
        }
        dbHelper.close();
    }

    public static List<IpoToday> getIpoTodayList(Context context) {
        List<IpoToday> ipoTodayList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.IPO_TODAY, null,
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            IpoToday ipoToday = parseEventCursor(cursor);
            ipoTodayList.add(ipoToday);
        }

        dbHelper.close();
        return ipoTodayList;
    }

    public static List<IpoTodayFull> getIpoTodayFullList(Context context) {
        List<IpoTodayFull> ipoTodayFullList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select * from " + DatabaseHelper.IPO_TODAY + " left join " + DatabaseHelper.IPO + " on " + DatabaseHelper.IPO_TODAY + "." + DatabaseHelper.STOCK_NAME + "=" + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_NAME, null);

        while (cursor.moveToNext()) {
            IpoTodayFull ipoTodayFull = parseIpoTodayCursor(cursor);
            ipoTodayFullList.add(ipoTodayFull);
        }

        dbHelper.close();
        return ipoTodayFullList;
    }

    public static void insertIpoList(Context context, List<IpoItem> ipoList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        sqliteDatabase.delete(DatabaseHelper.IPO, null, null);

        for (int i = 0; i < ipoList.size(); i++) {
            ContentValues values = createIpoItemValues(ipoList.get(i));
            sqliteDatabase.insert(DatabaseHelper.IPO, null, values);
        }
        dbHelper.close();
    }

    public static void updateIpoSchedule(Context context, List<IpoItem> ipoList) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        for (int i = 0; i < ipoList.size(); i++) {
            ContentValues values = createIpoScheduleValues(ipoList.get(i));
            sqliteDatabase.update(DatabaseHelper.IPO, values,
                    DatabaseHelper.STOCK_CODE + "=?",
                    new String[]{ipoList.get(i).getCode()});
        }
        dbHelper.close();
    }

    public static List<IpoItem> getIpoList(Context context) {
        List<IpoItem> ipoList = new ArrayList<IpoItem>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.IPO, null,
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            IpoItem ipoItem = parseIpoCursor(cursor);
            if (ipoItem != null) {
                ipoList.add(ipoItem);
            }
        }

        dbHelper.close();
        return ipoList;
    }

    private static ContentValues createIpoTodayValues(String event, String name) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.EVENT_NAME, event);
        values.put(DatabaseHelper.STOCK_NAME, name);
        return values;
    }

    private static ContentValues createIpoItemValues(IpoItem ipoItem) {
        ContentValues values = new ContentValues();
        if (ipoItem.getName() != null)
            values.put(DatabaseHelper.STOCK_NAME, ipoItem.getName());
        if (ipoItem.getCode() != null)
            values.put(DatabaseHelper.STOCK_CODE, ipoItem.getCode());
        if (ipoItem.getOfflineDate() != null) {
            values.put(DatabaseHelper.OFFLINE_DATE, ipoItem.getOfflineDate());
        }
        if (ipoItem.getUrl() != null)
            values.put(DatabaseHelper.IPO_URL, ipoItem.getUrl());
        return values;
    }

    private static ContentValues createIpoScheduleValues(IpoItem ipoItem) {
        ContentValues values = new ContentValues();
        if (ipoItem.getCode() != null)
            values.put(DatabaseHelper.STOCK_CODE, ipoItem.getCode());
        if (ipoItem.getNoticeDate() != null)
            values.put(DatabaseHelper.NOTICE_DATE, ipoItem.getNoticeDate());
        if (ipoItem.getInquiryDate() != null)
            values.put(DatabaseHelper.INQUIRY_DATE, ipoItem.getInquiryDate());
        if (ipoItem.getInquiryEndDate() != null)
            values.put(DatabaseHelper.INQUIRY_END_DATE, ipoItem.getInquiryEndDate());
        if (ipoItem.getAnnounceDate() != null)
            values.put(DatabaseHelper.ANNOUNCE_DATE, ipoItem.getAnnounceDate());
        if (ipoItem.getSuccessResultDate() != null)
            values.put(DatabaseHelper.SUCCESS_RESULT_DATE, ipoItem.getSuccessResultDate());
        if (ipoItem.getPaymentDate() != null)
            values.put(DatabaseHelper.PAYMENT_DATE, ipoItem.getPaymentDate());
        if (ipoItem.getListedDate() != null)
            values.put(DatabaseHelper.LISTED_DATE, ipoItem.getListedDate());
        return values;
    }

    private static IpoItem parseIpoCursor(Cursor cursor) {
        IpoItem ipoItem = new IpoItem();
        ipoItem.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STOCK_NAME)));
        ipoItem.setCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STOCK_CODE)));
        ipoItem.setNoticeDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTICE_DATE)));
        ipoItem.setInquiryDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.INQUIRY_DATE)));
        ipoItem.setInquiryEndDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.INQUIRY_END_DATE)));
        ipoItem.setAnnounceDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ANNOUNCE_DATE)));
        ipoItem.setOfflineDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OFFLINE_DATE)));
        ipoItem.setPaymentDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PAYMENT_DATE)));
        ipoItem.setSuccessResultDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUCCESS_RESULT_DATE)));
        ipoItem.setListedDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LISTED_DATE)));
        ipoItem.setUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IPO_URL)));
        //ipoItem.setIssuePrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.ISSUE_PRICE)));
        return ipoItem;
    }

    private static IpoToday parseEventCursor(Cursor cursor) {
        IpoToday ipoToday = new IpoToday();
        ipoToday.setEvent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_NAME)));
        ipoToday.setIpoName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STOCK_NAME)));
        return ipoToday;
    }

    private static IpoTodayFull parseIpoTodayCursor(Cursor cursor) {
        IpoTodayFull ipoTodayFull = new IpoTodayFull();
        ipoTodayFull.setEvent(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_NAME)));

        IpoItem ipoItem = parseIpoCursor(cursor);
        ipoTodayFull.setIpo(ipoItem);

        return ipoTodayFull;
    }
}
