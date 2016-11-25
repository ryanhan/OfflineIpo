package cn.ryanman.app.offlineipo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanyan on 11/24/2016.
 */

public class IpoToday {

    private List<IpoItem> successRateList; //发布中签率
    private List<IpoItem> successResultList; //发布中签结果
    private List<IpoItem> inquiryList; //询价
    private List<IpoItem> noticeList; //招股公告
    private List<IpoItem> announceList; //发行公告
    private List<IpoItem> listedList; //上市
    private List<IpoItem> offlineList; //网下发行
    private List<IpoItem> onlineList; //申购
    private List<IpoItem> roadshowList; //路演
    private List<IpoItem> paymentList; //缴款日

    public IpoToday() {
        successRateList = new ArrayList<IpoItem>();
        successResultList = new ArrayList<IpoItem>();
        inquiryList = new ArrayList<IpoItem>();
        noticeList = new ArrayList<IpoItem>();
        announceList = new ArrayList<IpoItem>();
        listedList = new ArrayList<IpoItem>();
        offlineList = new ArrayList<IpoItem>();
        onlineList = new ArrayList<IpoItem>();
        roadshowList = new ArrayList<IpoItem>();
        paymentList = new ArrayList<IpoItem>();
    }

    public List<IpoItem> getSuccessRateList() {
        return successRateList;
    }

    public void setSuccessRateList(List<IpoItem> successRateList) {
        this.successRateList = successRateList;
    }

    public List<IpoItem> getSuccessResultList() {
        return successResultList;
    }

    public void setSuccessResultList(List<IpoItem> successResultList) {
        this.successResultList = successResultList;
    }

    public List<IpoItem> getInquiryList() {
        return inquiryList;
    }

    public void setInquiryList(List<IpoItem> inquiryList) {
        this.inquiryList = inquiryList;
    }

    public List<IpoItem> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(List<IpoItem> noticeList) {
        this.noticeList = noticeList;
    }

    public List<IpoItem> getAnnounceList() {
        return announceList;
    }

    public void setAnnounceList(List<IpoItem> announceList) {
        this.announceList = announceList;
    }

    public List<IpoItem> getListedList() {
        return listedList;
    }

    public void setListedList(List<IpoItem> listedList) {
        this.listedList = listedList;
    }

    public List<IpoItem> getOfflineList() {
        return offlineList;
    }

    public void setOfflineList(List<IpoItem> offlineList) {
        this.offlineList = offlineList;
    }

    public List<IpoItem> getOnlineList() {
        return onlineList;
    }

    public void setOnlineList(List<IpoItem> onlineList) {
        this.onlineList = onlineList;
    }

    public List<IpoItem> getRoadshowList() {
        return roadshowList;
    }

    public void setRoadshowList(List<IpoItem> roadshowList) {
        this.roadshowList = roadshowList;
    }

    public List<IpoItem> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<IpoItem> paymentList) {
        this.paymentList = paymentList;
    }
}
