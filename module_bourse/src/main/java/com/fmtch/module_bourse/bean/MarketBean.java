package com.fmtch.module_bourse.bean;

import android.support.annotation.Nullable;

import java.math.BigDecimal;


/**
 * Created by wtc on 2019/5/20
 */
public class MarketBean {

    private String symbol;
    private String status;
    private String coin_name;
    private int coin_id;
    private int id;
    private String market_name;
    private int market_id;
    private int coin_decimals;
    private int market_decimals;
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
    private BigDecimal riseFall;
    private long timestamp;

    private int buyOrSell = -1;  //买入 1   卖出 2

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMarket_decimals() {
        return market_decimals;
    }

    public void setMarket_decimals(int market_decimals) {
        this.market_decimals = market_decimals;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public int getMarket_id() {
        return market_id;
    }

    public void setMarket_id(int market_id) {
        this.market_id = market_id;
    }

    public int getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(int buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private boolean isAddChoose;  //是否已加入自选

    public boolean isAddChoose() {
        return isAddChoose;
    }

    public void setAddChoose(boolean addChoose) {
        isAddChoose = addChoose;
    }

    public BigDecimal getRiseFall() {
        return riseFall;
    }

    public void setRiseFall(BigDecimal riseFall) {
        this.riseFall = riseFall;
    }

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof MarketBean)) {
            return false;
        }
        MarketBean m = (MarketBean) obj;
        return this.symbol.equals(m.getSymbol());
    }
}
