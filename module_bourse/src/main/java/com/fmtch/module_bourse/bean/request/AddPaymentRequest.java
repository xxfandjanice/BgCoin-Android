package com.fmtch.module_bourse.bean.request;

import java.io.Serializable;

public class AddPaymentRequest implements Serializable {

    private String name;
    private String account;
    private String bank_name;
    private String branch_name;

    private String alipay_account;
    private String alipay_qr_code;

    private String wechat_nickname;
    private String wechat_account;
    private String wechat_qr_code;

    private String sms_code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getAlipay_account() {
        return alipay_account;
    }

    public void setAlipay_account(String alipay_account) {
        this.alipay_account = alipay_account;
    }

    public String getAlipay_qr_code() {
        return alipay_qr_code;
    }

    public void setAlipay_qr_code(String alipay_qr_code) {
        this.alipay_qr_code = alipay_qr_code;
    }

    public String getWechat_nickname() {
        return wechat_nickname;
    }

    public void setWechat_nickname(String wechat_nickname) {
        this.wechat_nickname = wechat_nickname;
    }

    public String getWechat_account() {
        return wechat_account;
    }

    public void setWechat_account(String wechat_account) {
        this.wechat_account = wechat_account;
    }

    public String getWechat_qr_code() {
        return wechat_qr_code;
    }

    public void setWechat_qr_code(String wechat_qr_code) {
        this.wechat_qr_code = wechat_qr_code;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }
}
