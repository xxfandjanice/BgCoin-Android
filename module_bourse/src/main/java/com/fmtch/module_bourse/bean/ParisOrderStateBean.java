package com.fmtch.module_bourse.bean;

import android.text.TextUtils;

/**
 * Created by wtc on 2019/6/28
 */
public class ParisOrderStateBean {

    private long remainingTime;   //剩余时间

    /**
     * id : 2
     * side : SELL
     * symbol : USDT/CNY
     * status : 1
     * price : 0.10000000
     * number : 1.00000000
     * created_at : 2019-07-01 17:07:31
     * limit_pay_time : 10
     * cancel_type : 0
     */

    private int id;
    private String side;
    private String symbol;
    private int status;
    private String price;
    private String number;
    private String created_at;
    private int limit_pay_time;
    private int limit_finish_time;
    private int cancel_type;
    private String fee;
    private String payment_at;

    public int getLimit_finish_time() {
        return limit_finish_time;
    }

    public void setLimit_finish_time(int limit_finish_time) {
        this.limit_finish_time = limit_finish_time;
    }

    public String getPayment_at() {
        return payment_at;
    }

    public void setPayment_at(String payment_at) {
        this.payment_at = payment_at;
    }

    public String getFee() {
        return TextUtils.isEmpty(fee) ? "0" : fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getLimit_pay_time() {
        return limit_pay_time;
    }

    public void setLimit_pay_time(int limit_pay_time) {
        this.limit_pay_time = limit_pay_time;
    }

    public int getCancel_type() {
        return cancel_type;
    }

    public void setCancel_type(int cancel_type) {
        this.cancel_type = cancel_type;
    }
}
