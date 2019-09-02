package com.fmtch.module_bourse.bean;

/**
 * Created by wtc on 2019/6/29
 */
public class ParisCoinMarketBean {

    /**
     * id : 2
     * user_id : 8
     * coin_id : 1
     * side : BUY
     * symbol : USDT/CNY
     * avatar : null
     * username : 刘震
     * order_count : 0
     * finish_percent : 0
     * min_cny : 400.00000000
     * max_cny : 0.00000000
     * number : 100
     * price_type : 1
     * price : 0.10000000
     * bank : 1
     * alipay : 1
     * wechat : 0
     */

    private int id;
    private int user_id;
    private int coin_id;
    private String side;
    private String symbol;
    private String avatar;
    private String username;
    private String order_count;
    private String finish_percent;
    private String min_cny;
    private String max_cny;
    private String number;
    private int price_type;
    private String price;
    private int bank;
    private int alipay;
    private int wechat;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrder_count() {
        return order_count;
    }

    public void setOrder_count(String order_count) {
        this.order_count = order_count;
    }

    public String getFinish_percent() {
        return finish_percent;
    }

    public void setFinish_percent(String finish_percent) {
        this.finish_percent = finish_percent;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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
