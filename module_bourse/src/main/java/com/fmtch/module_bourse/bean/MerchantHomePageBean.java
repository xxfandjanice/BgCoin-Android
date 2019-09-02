package com.fmtch.module_bourse.bean;

import java.util.List;

/**
 * Created by wtc on 2019/6/26
 */
public class MerchantHomePageBean {

    /**
     * base_info : {"username":"刘震","created_at":"2019-06-18 23:00:00","kyc_status":1,"is_star":0,"is_black":0}
     * statistics : {"order_count":0,"finish_percent":"0%","avg_payment_time":0,"avg_confirm_time":0}
     * buy_list : [{"id":3,"side":"SELL","symbol":"USDT/CNY","bank":1,"alipay":1,"wechat":0,"min_cny":"400.00","max_cny":"100000.00","number":98,"price":"0.10"}]
     * sell_list : [{"id":2,"side":"BUY","symbol":"USDT/CNY","bank":1,"alipay":1,"wechat":0,"min_cny":"400.00","max_cny":"100000.00","number":98,"price":"0.10"}]
     */

    private BaseInfoBean base_info;
    private StatisticsBean statistics;
    private List<BuyOrSellBean> buy_list;
    private List<BuyOrSellBean> sell_list;

    public BaseInfoBean getBase_info() {
        return base_info;
    }

    public void setBase_info(BaseInfoBean base_info) {
        this.base_info = base_info;
    }

    public StatisticsBean getStatistics() {
        return statistics;
    }

    public void setStatistics(StatisticsBean statistics) {
        this.statistics = statistics;
    }

    public List<BuyOrSellBean> getBuy_list() {
        return buy_list;
    }

    public void setBuy_list(List<BuyOrSellBean> buy_list) {
        this.buy_list = buy_list;
    }

    public List<BuyOrSellBean> getSell_list() {
        return sell_list;
    }

    public void setSell_list(List<BuyOrSellBean> sell_list) {
        this.sell_list = sell_list;
    }

    public static class BaseInfoBean {
        /**
         * username : 刘震
         * created_at : 2019-06-18 23:00:00
         * kyc_status : 1
         * is_star : 0
         * is_black : 0
         */

        private String username;
        private String created_at;
        private String avatar;
        private int kyc_status;
        private int is_star;
        private int is_black;

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

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getKyc_status() {
            return kyc_status;
        }

        public void setKyc_status(int kyc_status) {
            this.kyc_status = kyc_status;
        }

        public int getIs_star() {
            return is_star;
        }

        public void setIs_star(int is_star) {
            this.is_star = is_star;
        }

        public int getIs_black() {
            return is_black;
        }

        public void setIs_black(int is_black) {
            this.is_black = is_black;
        }
    }

    public static class StatisticsBean {
        /**
         * order_count : 0
         * finish_percent : 0%
         * avg_payment_time : 0
         * avg_confirm_time : 0
         */

        private int order_count;
        private String finish_percent;
        private int avg_payment_time;
        private int avg_confirm_time;

        public int getOrder_count() {
            return order_count;
        }

        public void setOrder_count(int order_count) {
            this.order_count = order_count;
        }

        public String getFinish_percent() {
            return finish_percent;
        }

        public void setFinish_percent(String finish_percent) {
            this.finish_percent = finish_percent;
        }

        public int getAvg_payment_time() {
            return avg_payment_time;
        }

        public void setAvg_payment_time(int avg_payment_time) {
            this.avg_payment_time = avg_payment_time;
        }

        public int getAvg_confirm_time() {
            return avg_confirm_time;
        }

        public void setAvg_confirm_time(int avg_confirm_time) {
            this.avg_confirm_time = avg_confirm_time;
        }
    }

    public static class BuyOrSellBean {
        /**
         * id : 3
         * side : SELL
         * symbol : USDT/CNY
         * bank : 1
         * alipay : 1
         * wechat : 0
         * min_cny : 400.00
         * max_cny : 100000.00
         * number : 98
         * price : 0.10
         */

        private int id;
        private String side;
        private String symbol;
        private int bank;
        private int alipay;
        private int wechat;
        private int otc_num_decimals;
        private String min_cny;
        private String max_cny;
        private String number;
        private String price;

        public int getOtc_num_decimals() {
            return otc_num_decimals;
        }

        public void setOtc_num_decimals(int otc_num_decimals) {
            this.otc_num_decimals = otc_num_decimals;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
