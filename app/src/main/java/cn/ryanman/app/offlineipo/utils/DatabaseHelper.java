package cn.ryanman.app.offlineipo.utils;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ryan on 2016/11/25.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    public static final String DATABASENAME = "sizing_db";

    public static final String IPO = "ipo";
    public static final String ID = "id";
    public static final String STOCK_NAME = "stock_name";
    public static final String STOCK_CODE = "stock_code";
    public static final String OFFLINE_DATE = "offline_date"; //网下收购日
    public static final String SUCCESS_RESULT_DATE = "success_result_date";  //发布中签结果
    public static final String PAYMENT_DATE = "payment_date"; //缴款日
    public static final String ISSUE_PRICE = "issue_price"; //发行价格
    public static final String IPO_URL = "ipo_url"; //新股链接

    public static final String PERSON = "person";
    public static final String PERSON_NAME = "person_name";
    public static final String SH_CODE = "sh_code";

    public static final String MY_IPO = "my_ipo";
    public static final String HAVE_INQUIRY = "have_inquiry";
    public static final String HAVE_APPLY = "have_apply";
    public static final String HAVE_TRANSFER = "have_transfer";
    public static final String STOCK_SHARE = "stock_share";
    public static final String EARN_AMOUNT = "earn_amount";

    public static final String IPO_TODAY = "ipo_today";
    public static final String EVENT_NAME = "event_name";



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

        db.execSQL("create table if not exists " + IPO + " (" + STOCK_CODE
                + " text PRIMARY KEY, " + STOCK_NAME + " text, " + OFFLINE_DATE + " text, "
                + SUCCESS_RESULT_DATE + " text, " + PAYMENT_DATE + " text, " + ISSUE_PRICE + " text, "
                + IPO_URL + " text)");

        db.execSQL("create table if not exists " + PERSON + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PERSON_NAME + " text, " + SH_CODE + " text)");

        db.execSQL("create table if not exists " + MY_IPO + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + STOCK_CODE + " text, " + PERSON_NAME + " text, "
                + HAVE_INQUIRY + " integer, " + HAVE_APPLY + " integer, " + HAVE_TRANSFER + " integer, "
                + STOCK_SHARE + " integer, " + EARN_AMOUNT + " real)");

        db.execSQL("create table if not exists " + IPO_TODAY + " (" + ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EVENT_NAME + " text, " + STOCK_NAME  + " text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
