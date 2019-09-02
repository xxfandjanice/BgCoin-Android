package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/5/10
 */
public class Data {
    private String name;
    private String amounts;
    private String percent;
    private String coin;

    public Data(String name, String amounts, String percent, String coin) {
        this.name = name;
        this.amounts = amounts;
        this.percent = percent;
        this.coin = coin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmounts() {
        return amounts;
    }

    public void setAmounts(String amounts) {
        this.amounts = amounts;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
