package com.fmtch.module_bourse.bean.response;

import com.fmtch.module_bourse.bean.PaymentBean;

import java.io.Serializable;
import java.util.List;

public class PaymentResponse implements Serializable {

    private List<PaymentBean> bank;

    private List<PaymentBean> alipay;

    private List<PaymentBean> wechat;

    public List<PaymentBean> getBank() {
        return bank;
    }

    public void setBank(List<PaymentBean> bank) {
        this.bank = bank;
    }

    public List<PaymentBean> getAlipay() {
        return alipay;
    }

    public void setAlipay(List<PaymentBean> alipay) {
        this.alipay = alipay;
    }

    public List<PaymentBean> getWechat() {
        return wechat;
    }

    public void setWechat(List<PaymentBean> wechat) {
        this.wechat = wechat;
    }
}
