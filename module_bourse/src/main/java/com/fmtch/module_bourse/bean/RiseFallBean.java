package com.fmtch.module_bourse.bean;


/**
 * Created by wtc on 2019/5/10
 */
public class RiseFallBean {

    /**
     * symbol : ETH/USDT
     * status : 1
     * coin_name : ETH
     * coin_decimals : 8
     * market_name : USDT
     * price_step : 0.01000000
     * number_min : 0.00000000
     * number_step : 0.01000000
     * total_min : 0.00000000
     * open : 1.00000000
     * close : 1.10000000
     * high : 0.00000000
     * low : 0.00000000
     * number : 0.00000000
     * total : 0.00000000
     * change : 0.10000000000000009
     */

    private String symbol;
    private int status;
    private String coin_name;
    private int coin_decimals;
    private int market_decimals;
    private String market_name;
    private String price_step;
    private String number_min;
    private String number_step;
    private String total_min;
    private String open;
    private String close;
    private String high;
    private String low;
    private String number;
    private String total;
    private String change;
    private String close_usd;
    private String total_usd;

    public int getMarket_decimals() {
        return market_decimals;
    }

    public void setMarket_decimals(int market_decimals) {
        this.market_decimals = market_decimals;
    }

    public String getClose_usd() {
        return close_usd;
    }

    public void setClose_usd(String close_usd) {
        this.close_usd = close_usd;
    }

    public String getTotal_usd() {
        return total_usd;
    }

    public void setTotal_usd(String total_usd) {
        this.total_usd = total_usd;
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

    public int getCoin_decimals() {
        return coin_decimals;
    }

    public void setCoin_decimals(int coin_decimals) {
        this.coin_decimals = coin_decimals;
    }

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getPrice_step() {
        return price_step;
    }

    public void setPrice_step(String price_step) {
        this.price_step = price_step;
    }

    public String getNumber_min() {
        return number_min;
    }

    public void setNumber_min(String number_min) {
        this.number_min = number_min;
    }

    public String getNumber_step() {
        return number_step;
    }

    public void setNumber_step(String number_step) {
        this.number_step = number_step;
    }

    public String getTotal_min() {
        return total_min;
    }

    public void setTotal_min(String total_min) {
        this.total_min = total_min;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
