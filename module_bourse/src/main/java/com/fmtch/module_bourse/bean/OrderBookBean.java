package com.fmtch.module_bourse.bean;

import java.io.Serializable;

public class OrderBookBean implements Serializable {

    private String coin_id;
    private String symbol;
    private int bank;
    private int alipay;
    private int wechat;
    private String price;
    private String number;
    private String min_cny;
    private String max_cny;

    public String getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(String coin_id) {
        this.coin_id = coin_id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public int getAlipay() {
        return alipay;
    }

    public void setAlipay(int alipay) {
        this.alipay = alipay;
    }

    public int getWechat() {
        return wechat;
    }

    public void setWechat(int wechat) {
        this.wechat = wechat;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMin_cny() {
        return min_cny;
    }

    public void setMin_cny(String min_cny) {
        this.min_cny = min_cny;
    }

    public String getMax_cny() {
        return max_cny;
    }

    public void setMax_cny(String max_cny) {
        this.max_cny = max_cny;
    }
}
