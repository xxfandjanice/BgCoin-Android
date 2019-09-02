package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/6/29
 */
public class ParisCoinInfo {
    private String market_price;  //市场价
    private String close_price;   //盘口价

    public String getMarket_price() {
        return market_price;
    }

    public void setMarket_price(String market_price) {
        this.market_price = market_price;
    }

    public String getClose_price() {
        return close_price;
    }

    public void setClose_price(String close_price) {
        this.close_price = close_price;
    }
}
