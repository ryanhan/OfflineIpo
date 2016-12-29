package cn.ryanman.app.offlineipo.model;

import java.util.List;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpo {

    private IpoItem ipoItem;
    private List<String> userName;
    private int progress;
    private int stockShare;
    private double earnAmount;

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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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
