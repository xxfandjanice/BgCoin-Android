package com.fmtch.module_bourse.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Comparator;

/**
 * Created by wtc on 2019/5/15
 */
public class CoinBean implements Comparator, Parcelable {

    private Integer id;                 //币种id
    private String name;                //币种名称
    private Integer can_recharge;       //充值开关:0-不可充值  1-可充值
    private Integer can_withdraw;       //提现开关:0-不可提  1-可提
    private String withdraw_min;        //单次提现最小量
    private String withdraw_max;        //单次提现最大量
    private String withdraw_fee;        //提现手续费
    private Integer is_otc;             //是否支持法币交易:0-不支持  1-支持
    private Integer is_spot;            //是否支持币币交易:0-不支持  1-支持
    private Integer is_tag;             //是否需要显示标签:0-不需要  1-需要
    private String firstLetter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCan_recharge() {
        return can_recharge;
    }

    public void setCan_recharge(Integer can_recharge) {
        this.can_recharge = can_recharge;
    }

    public Integer getCan_withdraw() {
        return can_withdraw;
    }

    public void setCan_withdraw(Integer can_withdraw) {
        this.can_withdraw = can_withdraw;
    }

    public String getWithdraw_min() {
        return withdraw_min;
    }

    public void setWithdraw_min(String withdraw_min) {
        this.withdraw_min = withdraw_min;
    }

    public String getWithdraw_max() {
        return withdraw_max;
    }

    public void setWithdraw_max(String withdraw_max) {
        this.withdraw_max = withdraw_max;
    }

    public String getWithdraw_fee() {
        return withdraw_fee;
    }

    public void setWithdraw_fee(String withdraw_fee) {
        this.withdraw_fee = withdraw_fee;
    }

    public Integer getIs_otc() {
        return is_otc;
    }

    public void setIs_otc(Integer is_otc) {
        this.is_otc = is_otc;
    }

    public Integer getIs_spot() {
        return is_spot;
    }

    public void setIs_spot(Integer is_spot) {
        this.is_spot = is_spot;
    }

    public Integer getIs_tag() {
        return is_tag;
    }

    public void setIs_tag(Integer is_tag) {
        this.is_tag = is_tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstLetter() {
        if (!TextUtils.isEmpty(getName())) {
            this.firstLetter = getName().substring(0, 1).toUpperCase();
        } else {
            this.firstLetter = "#";
        }
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    @Override
    public int compare(Object o1, Object o2) {
        CoinBean coinBean1 = (CoinBean) o1;
        CoinBean coinBean2 = (CoinBean) o2;
        return coinBean1.getFirstLetter().compareTo(coinBean2.getFirstLetter());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.can_recharge);
        dest.writeValue(this.can_withdraw);
        dest.writeString(this.withdraw_min);
        dest.writeString(this.withdraw_max);
        dest.writeString(this.withdraw_fee);
        dest.writeValue(this.is_otc);
        dest.writeValue(this.is_spot);
        dest.writeValue(this.is_tag);
        dest.writeString(this.firstLetter);
    }

    public CoinBean() {
    }

    protected CoinBean(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.can_recharge = (Integer) in.readValue(Integer.class.getClassLoader());
        this.can_withdraw = (Integer) in.readValue(Integer.class.getClassLoader());
        this.withdraw_min = in.readString();
        this.withdraw_max = in.readString();
        this.withdraw_fee = in.readString();
        this.is_otc = (Integer) in.readValue(Integer.class.getClassLoader());
        this.is_spot = (Integer) in.readValue(Integer.class.getClassLoader());
        this.is_tag = (Integer) in.readValue(Integer.class.getClassLoader());
        this.firstLetter = in.readString();
    }

    public static final Creator<CoinBean> CREATOR = new Creator<CoinBean>() {
        @Override
        public CoinBean createFromParcel(Parcel source) {
            return new CoinBean(source);
        }

        @Override
        public CoinBean[] newArray(int size) {
            return new CoinBean[size];
        }
    };
}
