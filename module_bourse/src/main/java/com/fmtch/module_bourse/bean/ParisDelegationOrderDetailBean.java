package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/7/1
 */
public class ParisDelegationOrderDetailBean {

    /**
     * id : 1
     * status : 2
     * symbol : USDT/CNY
     * side : BUY
     * order_no : 1
     * price_type : 1
     * price : 0.10000000
     * number : 100.00000000
     * deal_number : 0.00000000
     * fee : 0.01000000
     * min_cny : 400.00000000
     * max_cny : 0.00000000
     * limit_kyc_level : 1
     */

    private int id;
    private int status;
    private String symbol;
    private String side;
    private String order_no;
    private int price_type;
    private String price;
    private String number;
    private String deal_number;
    private String fee;
    private String min_cny;
    private String max_cny;
    private String limit_kyc_level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public int getPrice_type() {
        return price_type;
    }

    public void setPrice_type(int price_type) {
        this.price_type = price_type;
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

    public String getDeal_number() {
        return deal_number;
    }

    public void setDeal_number(String deal_number) {
        this.deal_number = deal_number;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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

    public String getLimit_kyc_level() {
        return limit_kyc_level;
    }

    public void setLimit_kyc_level(String limit_kyc_level) {
        this.limit_kyc_level = limit_kyc_level;
    }
}
