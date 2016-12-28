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
import cn.ryanman.app.offlineipo.model.MyIpo;
import cn.ryanman.app.offlineipo.model.User;


public class DatabaseUtils {

    public static void createDatabase(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        dbHelper.close();
    }

    public static void saveUser(Context context, User user) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PERSON_NAME, user.getName());
        values.put(DatabaseHelper.SH_CODE, user.getCode());
        values.put(DatabaseHelper.SH_MARKET_VALUE, user.getMarket());
        if (user.getId() == -1) {
            sqliteDatabase.insert(DatabaseHelper.PERSON, null, values);
        } else {
            sqliteDatabase.update(DatabaseHelper.PERSON, values,
                    DatabaseHelper.ID + "=?",
                    new String[]{String.valueOf(user.getId())});
        }

        dbHelper.close();
    }

    public static void deleteUser(Context context, int userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        sqliteDatabase.delete(DatabaseHelper.PERSON, DatabaseHelper.ID + "=?", new String[]{String.valueOf(userId)});
        dbHelper.close();
    }

    public static List<User> getUserList(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        List<User> userList = new ArrayList<>();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.PERSON, null,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            User user = parseUserCursor(cursor);
            userList.add(user);
        }
        dbHelper.close();
        return userList;
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

    public static List<IpoTodayFull> getIpoTodayList(Context context) {
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

    public static void subscribe(Context context, String userName, String ipoCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PERSON_NAME, userName);
        values.put(DatabaseHelper.STOCK_CODE, ipoCode);
        sqliteDatabase.insert(DatabaseHelper.MY_IPO, null, values);

        Cursor cursor = sqliteDatabase.query(DatabaseHelper.PROGRESS, null,
                DatabaseHelper.STOCK_CODE + "=?", new String[]{ipoCode}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues values2 = new ContentValues();
            values.put(DatabaseHelper.STOCK_CODE, ipoCode);
            sqliteDatabase.insert(DatabaseHelper.PROGRESS, null, values2);
        }

        dbHelper.close();
    }

    public static boolean isSubscribed(Context context, String ipoCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.MY_IPO, null,
                DatabaseHelper.STOCK_CODE + "=?", new String[]{ipoCode}, null, null, null);
        int count = cursor.getCount();
        dbHelper.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getSubscriber(Context context, String ipoCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.MY_IPO, null,
                DatabaseHelper.STOCK_CODE + "=?", new String[]{ipoCode}, null, null, null);

        List<String> nameList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_NAME));
            nameList.add(name);
        }
        return nameList;
    }

    public static List<MyIpo> getAllSubscription(Context context, String userName) {
        List<MyIpo> myIpoList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;
        if (userName == null) {
            cursor = sqliteDatabase.rawQuery("select * from " + DatabaseHelper.MY_IPO + " left join " + DatabaseHelper.PROGRESS + " on "
                    + DatabaseHelper.MY_IPO + "." + DatabaseHelper.STOCK_CODE + "=" + DatabaseHelper.PROGRESS + "." + DatabaseHelper.STOCK_CODE
                    + " left join " + DatabaseHelper.IPO + " on " + DatabaseHelper.MY_IPO + "." + DatabaseHelper.STOCK_CODE + "=" + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_CODE, null);
        } else {
            cursor = sqliteDatabase.rawQuery("select * from " + DatabaseHelper.MY_IPO + " left join " + DatabaseHelper.PROGRESS + " on "
                    + DatabaseHelper.MY_IPO + "." + DatabaseHelper.STOCK_CODE + "=" + DatabaseHelper.PROGRESS + "." + DatabaseHelper.STOCK_CODE
                    + " left join " + DatabaseHelper.IPO + " on " + DatabaseHelper.MY_IPO + "." + DatabaseHelper.STOCK_CODE + "=" + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_CODE
                    + " where " + DatabaseHelper.PERSON_NAME + "=?", new String[]{userName});
        }
        while (cursor.moveToNext()) {
            MyIpo myIpo = parseMyIpoCursor(cursor);
            if (myIpo != null) {
                myIpoList.add(myIpo);
            }
        }
        dbHelper.close();
        return myIpoList;
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

    private static User parseUserCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID)));
        user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_NAME)));
        user.setCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SH_CODE)));
        user.setMarket(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SH_MARKET_VALUE)));
        return user;
    }

    private static MyIpo parseMyIpoCursor(Cursor cursor) {
        MyIpo myIpo = new MyIpo();
        IpoItem ipoItem = parseIpoCursor(cursor);
        myIpo.setIpoItem(ipoItem);
        myIpo.setUserName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_NAME)));
        myIpo.setStockShare(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK_SHARE)));
        myIpo.setEarnAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.EARN_AMOUNT)));

        int hasInquiry = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.HAS_INQUIRY));
        int hasApply = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.HAS_APPLY));
        int hasPay = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.HAS_PAY));

        if (hasInquiry == 0) {
            myIpo.setInquiry(false);
        } else if (hasInquiry == 1) {
            myIpo.setInquiry(true);
        }

        if (hasApply == 0) {
            myIpo.setApply(false);
        } else if (hasApply == 1) {
            myIpo.setApply(true);
        }

        if (hasPay == 0) {
            myIpo.setPay(false);
        } else if (hasPay == 1) {
            myIpo.setPay(true);
        }

        return myIpo;
    }
}
