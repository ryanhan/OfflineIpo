package cn.ryanman.app.offlineipo.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ryan on 2016/11/25.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String DATABASENAME = "offline_ipo_db";

    public static final String IPO = "ipo";
    public static final String ID = "id";
    public static final String STOCK_NAME = "stock_name";
    public static final String STOCK_CODE = "stock_code";
    public static final String M_STOCK_CODE = "m_stock_code";
    public static final String OFFLINE_DATE = "offline_date"; //网下收购日
    public static final String LISTED_DATE = "listed_date"; //上市日
    public static final String ISSUE_PRICE = "issue_price"; //发行价格
    public static final String ESTIMATE_PRICE = "estimate_price"; //发行价格
    public static final String IPO_URL = "ipo_url"; //新股链接

    public static final String MY_IPO = "my_ipo";
    public static final String STOCK_SHARE = "stock_share";
    public static final String EARN_AMOUNT = "earn_amount";
    public static final String PERSON_NUMBER = "person_number";

    public static final String IPO_TODAY = "ipo_today";
    public static final String EVENT_NAME = "event_name";

    public static final String SOLD_DATE = "sold_date";
    public static final String SOLD_PRICE = "sold_price";

    //private Context context;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        //this.context = context;
    }

    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists " + IPO + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + STOCK_CODE + " text, " + STOCK_NAME + " text, " + OFFLINE_DATE + " text, "
                + LISTED_DATE + " text, " + ESTIMATE_PRICE + " real," + ISSUE_PRICE + " real, " + IPO_URL + " text)");

        db.execSQL("create table if not exists " + MY_IPO + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + M_STOCK_CODE + " text, " + PERSON_NUMBER + " integer, "
                + STOCK_SHARE + " integer, " + SOLD_PRICE + " text, " + EARN_AMOUNT + " real, " + SOLD_DATE + " text)");

        db.execSQL("create table if not exists " + IPO_TODAY + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EVENT_NAME + " text, " + STOCK_NAME + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
