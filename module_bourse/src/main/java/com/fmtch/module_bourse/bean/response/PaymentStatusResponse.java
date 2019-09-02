package com.fmtch.module_bourse.bean.response;

import com.fmtch.module_bourse.bean.PaymentBean;

import java.io.Serializable;
import java.util.List;

public class PaymentStatusResponse implements Serializable {

    private int bank;

    private int alipay;

    private int wechat;

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public int getAlipay() {
        return alipay;
    }

    public void setAlipay(int alipay) {
        this.alipay = alipay;
    }

    public int getWechat() {
        return wechat;
    }

    public void setWechat(int wechat) {
        this.wechat = wechat;
    }
}
