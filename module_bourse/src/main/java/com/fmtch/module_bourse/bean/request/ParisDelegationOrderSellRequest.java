package com.fmtch.module_bourse.bean.request;

/**
 * Created by wtc on 2019/6/29
 */
public class ParisDelegationOrderSellRequest {
    private int coin_id;                      //币种ID
    private int price_type;                   //定价方式（1-固定价格 2-浮动价格）
    private String sell_price;                //购买价格
    private String sell_number;               //购买数量
    private int limit_kyc_level;              //认证等级（1-KYC1  2-KYC2    3-KYC3）
    private String limit_register_date;       //注册时间
    private int limit_pay_time;               //付款时间（10-10分钟    15-15分钟    20-20分钟 ）
    private String remark;                    //说明


    public ParisDelegationOrderSellRequest(int coin_id, int price_type, String sell_price, String sell_number, int limit_kyc_level, String limit_register_date, int limit_pay_time, String remark) {
        this.coin_id = coin_id;
        this.price_type = price_type;
        this.sell_price = sell_price;
        this.sell_number = sell_number;
        this.limit_kyc_level = limit_kyc_level;
        this.limit_register_date = limit_register_date;
        this.limit_pay_time = limit_pay_time;
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getLimit_pay_time() {
        return limit_pay_time;
    }

    public void setLimit_pay_time(int limit_pay_time) {
        this.limit_pay_time = limit_pay_time;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public int getPrice_type() {
        return price_type;
    }

    public void setPrice_type(int price_type) {
        this.price_type = price_type;
    }

    public String getSell_price() {
        return sell_price;
    }

    public void setSell_price(String sell_price) {
        this.sell_price = sell_price;
    }

    public String getSell_number() {
        return sell_number;
    }

    public void setSell_number(String sell_number) {
        this.sell_number = sell_number;
    }

    public int getLimit_kyc_level() {
        return limit_kyc_level;
    }

    public void setLimit_kyc_level(int limit_kyc_level) {
        this.limit_kyc_level = limit_kyc_level;
    }

    public String getLimit_register_date() {
        return limit_register_date;
    }

    public void setLimit_register_date(String limit_register_date) {
        this.limit_register_date = limit_register_date;
    }
}
