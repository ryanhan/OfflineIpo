package cn.ryanman.app.offlineipo.model;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpo {

    private IpoItem ipoItem;
    private String userName;
    private boolean hasInquiry;
    private boolean hasApply;
    private boolean hasPay;
    private int stockShare;
    private double earnAmount;

    public IpoItem getIpoItem() {
        return ipoItem;
    }

    public void setIpoItem(IpoItem ipoItem) {
        this.ipoItem = ipoItem;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean hasInquiry() {
        return hasInquiry;
    }

    public void setInquiry(boolean hasInquiry) {
        this.hasInquiry = hasInquiry;
    }

    public boolean hasApply() {
        return hasApply;
    }

    public void setApply(boolean hasApply) {
        this.hasApply = hasApply;
    }

    public boolean hasPay() {
        return hasPay;
    }

    public void setPay(boolean hasPay) {
        this.hasPay = hasPay;
    }

    public int getStockShare() {
        return stockShare;
    }

    public void setStockShare(int stockShare) {
        this.stockShare = stockShare;
    }

    public double getEarnAmount() {
        return earnAmount;
    }

    public void setEarnAmount(double earnAmount) {
        this.earnAmount = earnAmount;
    }
}
