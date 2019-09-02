package com.fmtch.module_bourse.bean;

import java.io.Serializable;

/**
 * Created by wtc on 2019/6/29
 */
public class ParisCoinBean implements Serializable {

    /**
     * id : 1
     * name : USDT
     * otc_fee : 0.00200000
     * otc_num_decimals : 2
     */

    private int id;
    private String name;
    private String otc_fee;
    private int otc_num_decimals;
    private String otc_deposit;
    private double otc_float_range;

    public double getOtc_float_range() {
        return otc_float_range;
    }

    public void setOtc_float_range(double otc_float_range) {
        this.otc_float_range = otc_float_range;
    }

    public String getOtc_deposit() {
        return otc_deposit;
    }

    public void setOtc_deposit(String otc_deposit) {
        this.otc_deposit = otc_deposit;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtc_fee() {
        return otc_fee;
    }

    public void setOtc_fee(String otc_fee) {
        this.otc_fee = otc_fee;
    }

    public int getOtc_num_decimals() {
        return otc_num_decimals;
    }

    public void setOtc_num_decimals(int otc_num_decimals) {
        this.otc_num_decimals = otc_num_decimals;
    }
}
