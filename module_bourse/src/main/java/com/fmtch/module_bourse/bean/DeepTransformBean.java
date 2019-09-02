package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/5/22
 */
public class DeepTransformBean {
    //买入
    private String buyAmount;
    private String buyPrice;
    private int buyPercent;
    //卖出
    private String sellAmount;
    private String sellPrice;
    private int sellPercent;

    public String getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(String buyAmount) {
        this.buyAmount = buyAmount;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getBuyPercent() {
        return buyPercent;
    }

    public void setBuyPercent(int buyPercent) {
        this.buyPercent = buyPercent;
    }


    public String getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(String sellAmount) {
        this.sellAmount = sellAmount;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getSellPercent() {
        return sellPercent;
    }

    public void setSellPercent(int sellPercent) {
        this.sellPercent = sellPercent;
    }
}
