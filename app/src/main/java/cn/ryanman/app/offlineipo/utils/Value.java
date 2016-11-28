package cn.ryanman.app.offlineipo.utils;

import java.util.HashMap;

/**
 * Created by hanyan on 11/21/2016.
 */

public class Value {

    public static final String PACKAGENAME = "cn.ryanman.app.offlineipo";

    public static final String IpoList = "http://ipo.sseinfo.com/info/commonQuery.do?sqlId=COMMON_SSE_IPO_IPO_LIST_L";
    public static final String IpoToday = "http://ipo.sseinfo.com/info/ipoInfoDisplay/searchTodayEvents.do"; //?search_date=2016-11-25";

    public static final int CONNECT_TIMEOUT = 3000;
    public static final int READ_TIMEOUT = 5000;

    public static final String NOTICE = "notice"; //招股公告
    public static final String INQUIRY = "inquiry"; //询价
    public static final String ROADSHOW = "roadshow"; //路演
    public static final String ANNOUNCE = "announce"; //发行公告
    public static final String OFFLINE = "offline"; //网下发行
    public static final String ONLINE = "online"; //申购
    public static final String SUCCESS_RATE = "success_rate"; //发布中签率
    public static final String SUCCESS_RESULT = "success_result"; //发布中签结果
    public static final String PAYMENT = "payment"; //缴款日
    public static final String LISTED = "listed"; //上市

    public static final String[] eventArray = {NOTICE, INQUIRY, ROADSHOW, ANNOUNCE, OFFLINE, ONLINE, SUCCESS_RATE, SUCCESS_RESULT, PAYMENT, LISTED};

    public static final HashMap<String, Integer> eventMap = new HashMap<>();

    static {
        eventMap.put(NOTICE, 0);
        eventMap.put(INQUIRY, 1);
        eventMap.put(ROADSHOW, 2);
        eventMap.put(ANNOUNCE, 3);
        eventMap.put(OFFLINE, 4);
        eventMap.put(ONLINE, 5);
        eventMap.put(SUCCESS_RATE, 6);
        eventMap.put(SUCCESS_RESULT, 7);
        eventMap.put(PAYMENT, 8);
        eventMap.put(LISTED, 9);
    }
}
