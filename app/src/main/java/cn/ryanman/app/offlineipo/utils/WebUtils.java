package cn.ryanman.app.offlineipo.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cn.ryanman.app.offlineipo.model.IpoItem;
import cn.ryanman.app.offlineipo.model.IpoToday;

/**
 * Created by ryan on 2016/11/25.
 */

public class WebUtils {

    public static List<IpoToday> getIpoToday() throws Exception{
        List<IpoToday> ipoTodayList = new ArrayList<>();
        String result = GetJson(Value.IpoToday);
        JSONObject json = new JSONObject(result);
        JSONObject resultJson = json.getJSONObject("returnData");

        JSONArray successRateArray = resultJson.getJSONArray("ANNOUNCE_SUCCESS_RATE_DATE_LIST"); //发布中签率
        for (int i = 0; i < successRateArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.SUCCESS_RATE);
            ipoToday.setIpoName(successRateArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray successResultArray = resultJson.getJSONArray("ANNOUNCE_SUCCESS_RATE_RESULT_DATE_LIST"); //发布中签结果
        for (int i = 0; i < successResultArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.SUCCESS_RESULT);
            ipoToday.setIpoName(successResultArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray inquiryArray = resultJson.getJSONArray("INQUIRY_DATE_LIST"); //询价
        for (int i = 0; i < inquiryArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.INQUIRY);
            ipoToday.setIpoName(inquiryArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray noticeArray = resultJson.getJSONArray("IPO_NOTICE_DATE_LIST"); //招股公告
        for (int i = 0; i < noticeArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.NOTICE);
            ipoToday.setIpoName(noticeArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray announceArray = resultJson.getJSONArray("ISSUANCE_ANNOUNCEMENT_DATE_LIST"); //发行公告
        for (int i = 0; i < announceArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.ANNOUNCE);
            ipoToday.setIpoName(announceArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray listedArray = resultJson.getJSONArray("LISTED_DATE_LIST"); //上市
        for (int i = 0; i < listedArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.LISTED);
            ipoToday.setIpoName(listedArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray offlineArray = resultJson.getJSONArray("OFFLINE_ISSUANCE_DATE_LIST"); //网下发行
        for (int i = 0; i < offlineArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.OFFLINE);
            ipoToday.setIpoName(offlineArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray onlineArray = resultJson.getJSONArray("ONLINE_ISSUANCE_DATE_LIST"); //申购
        for (int i = 0; i < onlineArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.ONLINE);
            ipoToday.setIpoName(onlineArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray roadshowArray = resultJson.getJSONArray("ONLINE_ROADSHOW_DATE_LIST"); //路演
        for (int i = 0; i < roadshowArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.ROADSHOW);
            ipoToday.setIpoName(roadshowArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        JSONArray paymentArray = resultJson.getJSONArray("PAYMENT_DATE_LIST"); //缴款日
        for (int i = 0; i < paymentArray.length(); i++) {
            IpoToday ipoToday = new IpoToday();
            ipoToday.setEvent(Value.PAYMENT);
            ipoToday.setIpoName(paymentArray.getString(i));
            ipoTodayList.add(ipoToday);
        }

        return ipoTodayList;
    }


    public static List<IpoItem> getIpoItems() throws Exception {
        List<IpoItem> ipoItems = new ArrayList<IpoItem>();
        String result = GetJson(Value.IpoList);

        JSONObject json = new JSONObject(result);
        JSONArray resultArray = json.getJSONArray("result");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject ipoJson = resultArray.getJSONObject(i);
            IpoItem ipoItem = new IpoItem();
            try {
                ipoItem.setName(ipoJson.getString("SECURITY_NAME"));
                ipoItem.setCode(ipoJson.getString("SECURITY_CODE"));
            }
            catch (Exception e){
                continue;
            }

            String listedDate = ipoJson.getString("LISTED_DATE");
            if (!listedDate.equals("-") && AppUtils.daysAfter(listedDate) > 60){
                continue;
            }

            try {
                ipoItem.setIssuePrice(ipoJson.getDouble("ISSUE_PRICE"));
            }catch (Exception e){
                ipoItem.setIssuePrice(0);
            }

            ipoItem.setOfflineDate(ipoJson.getString("OFFLINE_ISSUANCE_END_DATE"));
            ipoItem.setUrl(ipoJson.getString("ANNOUNCEMENT_URL"));
            ipoItems.add(ipoItem);
        }
        return ipoItems;
    }

    public static List<IpoItem> getIpoSchedule() throws Exception{
        List<IpoItem> ipoItems = new ArrayList<IpoItem>();
        String result = GetJson(Value.IpoSchedule);
        JSONObject json = new JSONObject(result);
        JSONArray resultArray = json.getJSONArray("result");
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject ipoJson = resultArray.getJSONObject(i);
            IpoItem ipoItem = new IpoItem();

            try {
                ipoItem.setCode(ipoJson.getString("SECURITY_CODE"));
            } catch (Exception e) {
                continue;
            }

            String listedDate = ipoJson.getString("LISTED_DATE");
            if (!listedDate.equals("-") && AppUtils.daysAfter(listedDate) > 60){
                continue;
            }

            ipoItem.setNoticeDate(ipoJson.getString("IPO_NOTICE_DATE")); //招股公告
            ipoItem.setInquiryDate(ipoJson.getString("INQUIRY_DATE")); //询价开始
            ipoItem.setInquiryEndDate(ipoJson.getString("INQUIRY_DATE_END")); //询价结束
            ipoItem.setAnnounceDate(ipoJson.getString("ISSUANCE_ANNOUNCEMENT_DATE")); //发行公告
            ipoItem.setSuccessResultDate(ipoJson.getString("ANNOUNCE_SUCCESS_RATE_RESULT_DATE")); //中签结果
            ipoItem.setPaymentDate(ipoJson.getString("PAYMENT_END_DATE")); //缴款日
            if (!ipoJson.getString("LISTED_DATE").equals("-")){
                ipoItem.setListedDate(ipoJson.getString("LISTED_DATE")); //上市日
            }
        }

        return ipoItems;
    }


    public static String GetJson(String url) throws IOException {

        HttpURLConnection urlConn = null;
        try {
            urlConn = (HttpURLConnection) new URL(url).openConnection();
            urlConn.addRequestProperty("accept", "application/json");
            urlConn.setConnectTimeout(Value.CONNECT_TIMEOUT);
            urlConn.setReadTimeout(Value.READ_TIMEOUT);
            int responseCode = urlConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readInputStream(urlConn);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
    }

    private static String readInputStream(HttpURLConnection urlConn)
            throws IOException {
        Charset charset = Charset.forName("UTF-8");
        InputStreamReader stream = new InputStreamReader(
                urlConn.getInputStream(), charset);
        BufferedReader reader = new BufferedReader(stream);
        StringBuffer responseBuffer = new StringBuffer();
        String read = "";
        while ((read = reader.readLine()) != null) {
            responseBuffer.append(read);
        }
        return responseBuffer.toString();
    }

}
