package cn.ryanman.app.offlineipo.model;

/**
 * Created by hanyan on 11/21/2016.
 */

public class IpoItem {

    private int id;
    private String name;
    private String code;
    private String noticeDate; //招股公告
    private String inquiryDate; //询价开始
    private String inquiryEndDate; //询价结束
    private String announceDate; //发行公告
    private String offlineDate; //线下申购日
    private String successResultDate;  //发布中签结果
    private String paymentDate; //缴款日
    private String listedDate; //上市日
    private double estimatePrice; //估价
    private double issuePrice;  //发行价
    private String url;
    private boolean applied;
    private Status progress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(String inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public String getInquiryEndDate() {
        return inquiryEndDate;
    }

    public void setInquiryEndDate(String inquiryEndDate) {
        this.inquiryEndDate = inquiryEndDate;
    }

    public String getAnnounceDate() {
        return announceDate;
    }

    public void setAnnounceDate(String announceDate) {
        this.announceDate = announceDate;
    }

    public String getOfflineDate() {
        return offlineDate;
    }

    public void setOfflineDate(String offlineDate) {
        this.offlineDate = offlineDate;
    }

    public String getSuccessResultDate() {
        return successResultDate;
    }

    public void setSuccessResultDate(String successResultDate) {
        this.successResultDate = successResultDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getListedDate() {
        return listedDate;
    }

    public void setListedDate(String listedDate) {
        this.listedDate = listedDate;
    }

    public double getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(double estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public double getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(double issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public Status getProgress() {
        return progress;
    }

    public void setProgress(Status progress) {
        this.progress = progress;
    }
}
