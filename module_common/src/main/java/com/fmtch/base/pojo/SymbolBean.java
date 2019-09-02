package com.fmtch.base.pojo;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by wtc on 2019/8/15
 */
@RealmClass
public class SymbolBean implements RealmModel, Serializable {

    /**
     * id : 1
     * symbol : BTC/USDT
     * status : 1
     * coin_name : BTC
     * coin_id : 2
     * market_name : USDT
     * market_id : 1
     * price_decimals : 2
     * price_step : 0.00010000
     * number_decimals : 6
     * number_step : 0.00010000
     * number_min : 0.00010000
     * total_min : 0.00000100
     */

    @PrimaryKey
    private int id;
    private String symbol;
    private int status;
    private String coin_name;
    private int coin_id;
    private String market_name;
    private int market_id;
    private int price_decimals;
    private String price_step;
    private int number_decimals;
    private String number_step;
    private String number_min;
    private String total_min;
    private boolean star;

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public int getMarket_id() {
        return market_id;
    }

    public void setMarket_id(int market_id) {
        this.market_id = market_id;
    }

    public int getPrice_decimals() {
        return price_decimals;
    }

    public void setPrice_decimals(int price_decimals) {
        this.price_decimals = price_decimals;
    }

    public String getPrice_step() {
        return price_step;
    }

    public void setPrice_step(String price_step) {
        this.price_step = price_step;
    }

    public int getNumber_decimals() {
        return number_decimals;
    }

    public void setNumber_decimals(int number_decimals) {
        this.number_decimals = number_decimals;
    }

    public String getNumber_step() {
        return number_step;
    }

    public void setNumber_step(String number_step) {
        this.number_step = number_step;
    }

    public String getNumber_min() {
        return number_min;
    }

    public void setNumber_min(String number_min) {
        this.number_min = number_min;
    }

    public String getTotal_min() {
        return total_min;
    }

    public void setTotal_min(String total_min) {
        this.total_min = total_min;
    }

    @Override
    public String toString() {
        return "SymbolBean{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", status=" + status +
                ", coin_name='" + coin_name + '\'' +
                ", coin_id=" + coin_id +
                ", market_name='" + market_name + '\'' +
                ", market_id=" + market_id +
                ", price_decimals=" + price_decimals +
                ", price_step='" + price_step + '\'' +
                ", number_decimals=" + number_decimals +
                ", number_step='" + number_step + '\'' +
                ", number_min='" + number_min + '\'' +
                ", total_min='" + total_min + '\'' +
                '}';
    }
}
