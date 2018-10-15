package cn.ryanman.app.offlineipo.model;

/**
 * Created by ryanh on 2016/12/27.
 */

public class MyIpo {

    private IpoItem ipoItem;
    private int personNumber;
    private int stockShare;
    private double earnAmount;
    private String soldDate;
    private String soldPrice;

    public IpoItem getIpoItem() {
        return ipoItem;
    }

    public void setIpoItem(IpoItem ipoItem) {
        this.ipoItem = ipoItem;
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
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

    public String getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(String soldDate) {
        this.soldDate = soldDate;
    }

    public String getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(String soldPrice) {
        this.soldPrice = soldPrice;
    }
}
