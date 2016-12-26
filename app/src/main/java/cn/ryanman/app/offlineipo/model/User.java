package cn.ryanman.app.offlineipo.model;

/**
 * Created by hanyan on 11/21/2016.
 */

public class User {

    private int id;
    private String name;
    private String code;
    private String market;

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

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
