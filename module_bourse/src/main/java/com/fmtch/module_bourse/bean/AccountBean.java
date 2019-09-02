package com.fmtch.module_bourse.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created by wtc on 2019/5/21
 */
public class AccountBean implements Serializable, Comparator {

    private String id;                  //总账户id
    private String user_id;             //用户id
    private String coin_id;             //币种id
    private String available;           //可用余额
    private String disabled;            //冻结余额
    private String address;             //地址
    private String tag;                 //标签

    private BigDecimal btc_num;             //转成btc数量
    private BigDecimal rate_percent;        //占比
    private int tag_color;

    private Coin coin;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(String coin_id) {
        this.coin_id = coin_id;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public BigDecimal getBtc_num() {
        return btc_num;
    }

    public void setBtc_num(BigDecimal btc_num) {
        this.btc_num = btc_num;
    }

    public BigDecimal getRate_percent() {
        return rate_percent;
    }

    public void setRate_percent(BigDecimal rate_percent) {
        this.rate_percent = rate_percent;
    }

    public int getTag_color() {
        return tag_color;
    }

    public void setTag_color(int tag_color) {
        this.tag_color = tag_color;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public static class Coin implements Serializable{
        private int id;                 //币种id
        private String name;                //币种名称
        private int can_recharge;       //充值开关:0-不可充值  1-可充值
        private int can_withdraw;       //提现开关:0-不可提  1-可提
        private String withdraw_min;        //单次提现最小量
        private String withdraw_max;        //单次提现最大量
        private String withdraw_fee;        //提现手续费
        private int is_otc;             //是否支持法币交易:0-不支持  1-支持
        private int is_spot;            //是否支持币币交易:0-不支持  1-支持
        private int is_tag;             //是否需要显示标签:0-不需要  1-需要
        private String withdraw_prompt; //提现提示
        private String recharge_prompt; //充值提示
        private String firstLetter;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCan_recharge() {
            return can_recharge;
        }

        public void setCan_recharge(int can_recharge) {
            this.can_recharge = can_recharge;
        }

        public int getCan_withdraw() {
            return can_withdraw;
        }

        public void setCan_withdraw(int can_withdraw) {
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

        public int getIs_otc() {
            return is_otc;
        }

        public void setIs_otc(int is_otc) {
            this.is_otc = is_otc;
        }

        public int getIs_spot() {
            return is_spot;
        }

        public void setIs_spot(int is_spot) {
            this.is_spot = is_spot;
        }

        public int getIs_tag() {
            return is_tag;
        }

        public void setIs_tag(int is_tag) {
            this.is_tag = is_tag;
        }

        public String getWithdraw_prompt() {
            return withdraw_prompt;
        }

        public void setWithdraw_prompt(String withdraw_prompt) {
            this.withdraw_prompt = withdraw_prompt;
        }

        public String getRecharge_prompt() {
            return recharge_prompt;
        }

        public void setRecharge_prompt(String recharge_prompt) {
            this.recharge_prompt = recharge_prompt;
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
    }

    @Override
    public int compare(Object o1, Object o2) {
        AccountBean accountBean1 = (AccountBean) o1;
        AccountBean accountBean2 = (AccountBean) o2;
        return accountBean1.getCoin().getFirstLetter().compareTo(accountBean2.getCoin().getFirstLetter());
    }

}
