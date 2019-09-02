package com.fmtch.module_bourse.bean.request;

import java.io.Serializable;

public class AddCoinAddressRequest implements Serializable {

    private String coin_id;

    private String address;

    private String note;

    private String tag;

    private String sms_code;

    private String google_code;

    private String email_code;

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

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getGoogle_code() {
        return google_code;
    }

    public void setGoogle_code(String google_code) {
        this.google_code = google_code;
    }

    public String getEmail_code() {
        return email_code;
    }

    public void setEmail_code(String email_code) {
        this.email_code = email_code;
    }
}
