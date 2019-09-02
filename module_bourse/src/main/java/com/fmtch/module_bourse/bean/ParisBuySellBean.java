package com.fmtch.module_bourse.bean;

import java.io.Serializable;

/**
 * Created by wtc on 2019/6/29
 */
public class ParisBuySellBean implements Serializable {

    private String username;
    private String created_at;
    private int kyc_status;
    private int order_count;
    private String finish_percent;
    private String avg_payment_time;
    private String avg_confirm_time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getKyc_status() {
        return kyc_status;
    }

    public void setKyc_status(int kyc_status) {
        this.kyc_status = kyc_status;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public String getFinish_percent() {
        return finish_percent;
    }

    public void setFinish_percent(String finish_percent) {
        this.finish_percent = finish_percent;
    }

    public String getAvg_payment_time() {
        return avg_payment_time;
    }

    public void setAvg_payment_time(String avg_payment_time) {
        this.avg_payment_time = avg_payment_time;
    }

    public String getAvg_confirm_time() {
        return avg_confirm_time;
    }

    public void setAvg_confirm_time(String avg_confirm_time) {
        this.avg_confirm_time = avg_confirm_time;
    }
}
