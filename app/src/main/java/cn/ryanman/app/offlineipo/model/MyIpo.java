package cn.ryanman.app.offlineipo.model;

import java.util.List;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpo {

    private IpoItem ipoItem;
    private List<String> userName;
    private List<Integer> stockShare;
    private List<Double> earnAmount;
    private String soldDate;

    public IpoItem getIpoItem() {
        return ipoItem;
    }

    public void setIpoItem(IpoItem ipoItem) {
        this.ipoItem = ipoItem;
    }

    public List<String> getUserName() {
        return userName;
    }

    public void setUserName(List<String> userName) {
        this.userName = userName;
    }

    public List<Integer> getStockShare() {
        return stockShare;
    }

    public void setStockShare(List<Integer> stockShare) {
        this.stockShare = stockShare;
    }

    public List<Double> getEarnAmount() {
        return earnAmount;
    }

    public void setEarnAmount(List<Double> earnAmount) {
        this.earnAmount = earnAmount;
    }

    public String getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(String soldDate) {
        this.soldDate = soldDate;
    }
}
