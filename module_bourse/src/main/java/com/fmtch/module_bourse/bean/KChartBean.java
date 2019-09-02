package com.fmtch.module_bourse.bean;

import com.google.gson.annotations.SerializedName;
import com.icechao.klinelib.entity.KLineEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class KChartBean extends KLineEntity {

    private long time_open;
    private long time_close;
    private float price_open;
    private float price_high;
    private float price_low;
    private float price_close;
    private float trades_number;
    private String trades_total;
    private String trades_count;

    @Override
    public Long getDate() {
        return time_open*1000;
    }

    @Override
    public float getOpenPrice() {
        return price_open;
    }

    @Override
    public float getHighPrice() {
        return price_high;
    }

    @Override
    public float getLowPrice() {
        return price_low;
    }

    @Override
    public float getClosePrice() {
        return price_close;
    }

    @Override
    public float getVolume() {
        return trades_number;
    }

    public long getTime_open() {
        return time_open;
    }

    public void setTime_open(long time_open) {
        this.time_open = time_open;
    }

    public long getTime_close() {
        return time_close;
    }

    public void setTime_close(long time_close) {
        this.time_close = time_close;
    }

    public float getPrice_open() {
        return price_open;
    }

    public void setPrice_open(float price_open) {
        this.price_open = price_open;
    }

    public float getPrice_high() {
        return price_high;
    }

    public void setPrice_high(float price_high) {
        this.price_high = price_high;
    }

    public float getPrice_low() {
        return price_low;
    }

    public void setPrice_low(float price_low) {
        this.price_low = price_low;
    }

    public float getPrice_close() {
        return price_close;
    }

    public void setPrice_close(float price_close) {
        this.price_close = price_close;
    }

    public float getTrades_number() {
        return trades_number;
    }

    public void setTrades_number(float trades_number) {
        this.trades_number = trades_number;
    }

    public String getTrades_total() {
        return trades_total;
    }

    public void setTrades_total(String trades_total) {
        this.trades_total = trades_total;
    }

    public String getTrades_count() {
        return trades_count;
    }

    public void setTrades_count(String trades_count) {
        this.trades_count = trades_count;
    }

    @Override
    public String toString() {
        return "KChartBean{" +
                "time_open=" + time_open +
                ", time_close=" + time_close +
                ", price_open=" + price_open +
                ", price_high=" + price_high +
                ", price_low=" + price_low +
                ", price_close=" + price_close +
                ", trades_number=" + trades_number +
                ", trades_total='" + trades_total + '\'' +
                ", trades_count='" + trades_count + '\'' +
                '}';
    }
}
