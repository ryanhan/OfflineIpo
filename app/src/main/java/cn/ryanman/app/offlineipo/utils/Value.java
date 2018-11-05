package cn.ryanman.app.offlineipo.utils;

import java.util.HashMap;

import cn.ryanman.app.offlineipo.model.Status;

/**
 * Created by hanyan on 11/21/2016.
 */

public class Value {

    public static final String PACKAGENAME = "cn.ryanman.app.offlineipo";

    public static final String IpoList = "http://ipo.sseinfo.com/info/commonQuery.do?sqlId=COMMON_SSE_IPO_IPO_LIST_L";
    public static final String IpoToday = "http://ipo.sseinfo.com/info/ipoInfoDisplay/searchTodayEvents.do?search_date=";

    public static final String NoticeList = "http://query.sse.com.cn/commonQuery.do?sqlId=COMMON_IPO_IPOXGGG_SEARCH_L&securityCode=";
    public static final String REFERER = "http://ipo.sseinfo.com/info/xjxx/index_xq.shtml?securityCode=";

    public static final String APPID = "5bc42241959d69631edef567";
    public static final String TOKEN = "f06f9e78a13b6fab1d2f47a8510d87cf";

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 5000;

    public static final String APPINFO = "appinfo";
    public static final String TIME = "time";

    public static final String DATE = "date";
    public static final String IPO_CODE = "ipo_code";
    public static final String IPO_NAME = "ipo_name";

    public static final int BY_MONTH = 0;
    public static final int BY_YEAR = 1;

    public static final String DATABASE_SPLIT = "##";
    public static final String SOLD_PRICE_SPLIT = ";";

    public static final String DATABASE_FILE_NAME = "myipo.db";

    public static final String[] eventArray = {Status.NOTICE.toString(), Status.INQUIRY.toString(), Status.ROADSHOW.toString(), Status.ANNOUNCE.toString(), Status.OFFLINE.toString(), Status.ONLINE.toString(), Status.SUCCESS_RATE.toString(), Status.SUCCESS_RESULT.toString(), Status.PAYMENT.toString(), Status.LISTED.toString()};

    public static final HashMap<String, Integer> eventMap = new HashMap<>();
    public static HashMap<String, Status> ipoTodayMap = new HashMap<>();

    static {
        for (int i = 0; i < eventArray.length; i++){
            eventMap.put(eventArray[i], i);
        }
    }
}
