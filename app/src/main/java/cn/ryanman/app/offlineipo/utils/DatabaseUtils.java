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
import cn.ryanman.app.offlineipo.model.Status;


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
            ContentValues values = createIpoTodayValues(ipoTodayList.get(i).getEvent().toString(), ipoTodayList.get(i).getIpoName());
            sqliteDatabase.insert(DatabaseHelper.IPO_TODAY, null, values);
        }
        dbHelper.close();
    }

    public static List<IpoTodayFull> getIpoTodayList(Context context) {
        List<IpoTodayFull> ipoTodayFullList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select * from " + DatabaseHelper.IPO_TODAY + " left join " + DatabaseHelper.IPO + " on " + DatabaseHelper.IPO_TODAY + "." + DatabaseHelper.STOCK_NAME + "=" + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_NAME + " left join " + DatabaseHelper.MY_IPO + " on " + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_CODE + "=" + DatabaseHelper.MY_IPO + "." + DatabaseHelper.M_STOCK_CODE, null);

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

    public static List<IpoItem> getIpoList(Context context) {
        List<IpoItem> ipoList = new ArrayList<IpoItem>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select * from " + DatabaseHelper.IPO + " left join "
                + DatabaseHelper.MY_IPO + " on " + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_CODE
                + "=" + DatabaseHelper.MY_IPO + "." + DatabaseHelper.M_STOCK_CODE, null);

        while (cursor.moveToNext()) {
            IpoItem ipoItem = parseIpoCursor(cursor);
            if (ipoItem != null && ipoItem.getOfflineDate() != null) {
                if (cursor.isNull(cursor.getColumnIndex(DatabaseHelper.M_STOCK_CODE))) {
                    ipoItem.setApplied(false);
                } else {
                    ipoItem.setApplied(true);
                }
                ipoList.add(ipoItem);
            }
        }

        dbHelper.close();
        return ipoList;
    }

    public static MyIpo getIpoDetail(Context context, String ipoCode) {
        MyIpo myIpo = new MyIpo();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.IPO, null,
                DatabaseHelper.STOCK_CODE + "=?", new String[]{ipoCode}, null, null, null);

        IpoItem ipoItem = null;
        while (cursor.moveToNext()) {
            ipoItem = parseIpoCursor(cursor);
            break;
        }
        if (ipoItem == null) {
            return null;
        }

        myIpo.setIpoItem(ipoItem);

        cursor = sqliteDatabase.query(DatabaseHelper.MY_IPO, null,
                DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode}, null, null, null);

        while (cursor.moveToNext()) {
            myIpo.getIpoItem().setApplied(true);
            myIpo.setPersonNumber(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PERSON_NUMBER)));
            myIpo.setEarnAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.EARN_AMOUNT)));
            myIpo.setStockShare(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK_SHARE)));
            myIpo.setSoldDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLD_DATE)));
            myIpo.setSoldPrice(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLD_PRICE)));
            break;
        }

        dbHelper.close();
        return myIpo;
    }


    public static void subscribe(Context context, String ipoCode, int personNumber) {
        if (isSubscribed(context, ipoCode)) {
            return;
        }
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PERSON_NUMBER, personNumber);
        values.put(DatabaseHelper.M_STOCK_CODE, ipoCode);
        sqliteDatabase.insert(DatabaseHelper.MY_IPO, null, values);
        dbHelper.close();
    }

    public static void unsubscribe(Context context, String ipoCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        sqliteDatabase.delete(DatabaseHelper.MY_IPO, DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode});

        dbHelper.close();
    }

    public static boolean isSubscribed(Context context, String ipoCode) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.MY_IPO, null,
                DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode}, null, null, null);
        int count = cursor.getCount();
        dbHelper.close();
        return count > 0 ? true : false;
    }

    public static void updateStockNumber(Context context, String ipoCode, int stockNumber) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.STOCK_SHARE, stockNumber);
        sqliteDatabase.update(DatabaseHelper.MY_IPO, values,
                DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode});
        dbHelper.close();
    }

    public static void updateEarnAmount(Context context, String ipoCode, String earn) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        double earnAmount = 0;
        try {
            earnAmount = Double.parseDouble(earn);
        } catch (Exception e) {
        }
        values.put(DatabaseHelper.EARN_AMOUNT, earnAmount);
        sqliteDatabase.update(DatabaseHelper.MY_IPO, values,
                DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode});
        dbHelper.close();
    }

    public static void updateSoldDate(Context context, String ipoCode, String soldDate) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SOLD_DATE, soldDate);
        sqliteDatabase.update(DatabaseHelper.MY_IPO, values,
                DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode});
        dbHelper.close();
    }

    public static void updateSoldPrice(Context context, String ipoCode, String soldPrice) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.SOLD_PRICE, soldPrice);
        sqliteDatabase.update(DatabaseHelper.MY_IPO, values,
                DatabaseHelper.M_STOCK_CODE + "=?", new String[]{ipoCode});
        dbHelper.close();
    }

    public static List<MyIpo> getAllSubscription(Context context) {
        List<MyIpo> myIpoList = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select * from " + DatabaseHelper.MY_IPO + " left join " + DatabaseHelper.IPO + " on " + DatabaseHelper.MY_IPO + "." + DatabaseHelper.M_STOCK_CODE + "=" + DatabaseHelper.IPO + "." + DatabaseHelper.STOCK_CODE, null);
        while (cursor.moveToNext()) {
            MyIpo myIpo = new MyIpo();
            myIpo.setIpoItem(parseIpoCursor(cursor));
            myIpo.getIpoItem().setApplied(true);
            myIpo.setPersonNumber(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PERSON_NUMBER)));
            myIpo.setEarnAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.EARN_AMOUNT)));
            myIpo.setStockShare(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK_SHARE)));
            myIpo.setSoldDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLD_DATE)));
            myIpo.setSoldPrice(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLD_PRICE)));
            myIpoList.add(myIpo);
        }
        dbHelper.close();
        return myIpoList;
    }

    public static boolean exportMyIpo(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(DatabaseHelper.MY_IPO, null,
                null, null, null, null, null);
        StringBuilder sb = new StringBuilder();
        while (cursor.moveToNext()) {
            String code = cursor.getString(cursor.getColumnIndex(DatabaseHelper.M_STOCK_CODE));
            int person = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.PERSON_NUMBER));
            double amount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.EARN_AMOUNT));
            int share = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.STOCK_SHARE));
            String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLD_DATE));
            String price = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLD_PRICE));
            sb.append(code).append(Value.DATABASE_SPLIT).append(person).append(Value.DATABASE_SPLIT).append(amount).append(Value.DATABASE_SPLIT).append(share).append(Value.DATABASE_SPLIT).append(date).append(Value.DATABASE_SPLIT).append(price).append("\n");
        }
        if (AppUtils.writeToFile(sb.toString(), Value.DATABASE_FILE_NAME)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean importMyIpo(Context context) {
        String content = AppUtils.readFromFile(Value.DATABASE_FILE_NAME);
        if (content == null) {
            return false;
        }
        DatabaseHelper dbHelper = new DatabaseHelper(context,
                DatabaseHelper.DATABASENAME);
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        sqliteDatabase.delete(DatabaseHelper.MY_IPO, null, null);
        String[] lines = content.trim().split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] != null && !lines[i].equals("")) {
                ContentValues values = new ContentValues();
                String[] unit = lines[i].trim().split(Value.DATABASE_SPLIT);
                values.put(DatabaseHelper.M_STOCK_CODE, unit[0]);
                values.put(DatabaseHelper.PERSON_NUMBER, Integer.parseInt(unit[1]));
                values.put(DatabaseHelper.EARN_AMOUNT, Double.parseDouble(unit[2]));
                values.put(DatabaseHelper.STOCK_SHARE, Integer.parseInt(unit[3]));
                if (!unit[4].equals("null")) {
                    values.put(DatabaseHelper.SOLD_DATE, unit[4]);
                }
                if (!unit[5].equals("null")) {
                    values.put(DatabaseHelper.SOLD_PRICE, unit[5]);
                }
                sqliteDatabase.insert(DatabaseHelper.MY_IPO, null, values);
            }
        }
        dbHelper.close();
        return true;
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
        if (ipoItem.getListedDate() != null) {
            values.put(DatabaseHelper.LISTED_DATE, ipoItem.getListedDate());
        }
        values.put(DatabaseHelper.ISSUE_PRICE, ipoItem.getIssuePrice());
        if (ipoItem.getUrl() != null)
            values.put(DatabaseHelper.IPO_URL, ipoItem.getUrl());
        return values;
    }

    private static IpoItem parseIpoCursor(Cursor cursor) {
        IpoItem ipoItem = new IpoItem();
        ipoItem.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STOCK_NAME)));
        ipoItem.setCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STOCK_CODE)));
        ipoItem.setOfflineDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.OFFLINE_DATE)));
        ipoItem.setListedDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.LISTED_DATE)));
        ipoItem.setUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.IPO_URL)));
        ipoItem.setIssuePrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.ISSUE_PRICE)));
        return ipoItem;
    }

    private static IpoTodayFull parseIpoTodayCursor(Cursor cursor) {
        IpoTodayFull ipoTodayFull = new IpoTodayFull();
        ipoTodayFull.setEvent(Status.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_NAME))));

        IpoItem ipoItem = parseIpoCursor(cursor);
        if (cursor.isNull(cursor.getColumnIndex(DatabaseHelper.M_STOCK_CODE))) {
            ipoItem.setApplied(false);
        } else {
            ipoItem.setApplied(true);
        }
        ipoTodayFull.setIpo(ipoItem);

        return ipoTodayFull;
    }

}
