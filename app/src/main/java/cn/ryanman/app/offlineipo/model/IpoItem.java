package cn.ryanman.app.offlineipo.model;

/**
 * Created by hanyan on 11/21/2016.
 */

public class IpoItem {

    private int id;
    private String name;
    private String code;
    private String offlineDate; //线下申购日
    private String successResultDate;  //发布中签结果
    private String paymentDate; //缴款日
    private double issuePrice;  //发行价
    private String url;

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

}
