package com.fmtch.module_bourse.bean.request;

/**
 * Created by wtc on 2019/6/29
 */
public class ParisDelegationOrderBuyRequest {
    private int coin_id;                     //币种ID
    private int price_type;                  //定价方式（1-固定价格 2-浮动价格）
    private String buy_price;                //购买价格
    private String buy_number;               //购买数量
    private int limit_kyc_level;             //认证等级（1-KYC1  2-KYC2    3-KYC3）
    private String limit_register_date;      //注册时间
    private String remark;                   //说明

    public ParisDelegationOrderBuyRequest(int coin_id, int price_type, String buy_price, String buy_number, int limit_kyc_level, String limit_register_date, String remark) {
        this.coin_id = coin_id;
        this.price_type = price_type;
        this.buy_price = buy_price;
        this.buy_number = buy_number;
        this.limit_kyc_level = limit_kyc_level;
        this.limit_register_date = limit_register_date;
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(String buy_price) {
        this.buy_price = buy_price;
    }

    public String getBuy_number() {
        return buy_number;
    }

    public void setBuy_number(String buy_number) {
        this.buy_number = buy_number;
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
