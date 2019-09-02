package com.fmtch.module_bourse.bean;

import java.io.Serializable;

public class CoinAddressBean implements Serializable {

    private String id;              //地址id

    private String coin_id;         //币种id

    private String address;         //提现地址

    private String note;            //地址备注

    private String tag;             //标签

    private int type;           //是否选择

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(String coin_id) {
        this.coin_id = coin_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
