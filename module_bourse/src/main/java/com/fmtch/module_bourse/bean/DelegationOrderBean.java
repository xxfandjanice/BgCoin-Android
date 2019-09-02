package com.fmtch.module_bourse.bean;

import java.io.Serializable;

/**
 * Created by wtc on 2019/6/27
 */
public class DelegationOrderBean implements Serializable {

    /**
     * id : 3
     * symbol : USDT/CNY
     * side : SELL
     * status : 0
     * price_type : 1
     * price : 0.10000000
     * number : 99.00000000
     * deal_number : 0.00000000
     * created_at : 2019-06-28 17:10:55
     */

    private int id;
    private String symbol;
    private String side;
    private int status;
    private int price_type;
    private int is_pause;
    private String price;
    private String number;
    private String deal_number;
    private String created_at;

    public int getIs_pause() {
        return is_pause;
    }

    public void setIs_pause(int is_pause) {
        this.is_pause = is_pause;
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

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
