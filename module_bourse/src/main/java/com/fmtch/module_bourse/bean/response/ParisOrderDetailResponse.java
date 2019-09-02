package com.fmtch.module_bourse.bean.response;

import com.fmtch.module_bourse.bean.PaymentBean;

import java.io.Serializable;
import java.util.List;

public class ParisOrderDetailResponse implements Serializable {

    private OrderInfo order_info;
    private OtherInfo other_info;
    private List<PaymentBean> payment_account;

    public OrderInfo getOrder_info() {
        return order_info;
    }

    public void setOrder_info(OrderInfo order_info) {
        this.order_info = order_info;
    }

    public OtherInfo getOther_info() {
        return other_info;
    }

    public void setOther_info(OtherInfo other_info) {
        this.other_info = other_info;
    }

    public List<PaymentBean> getPayment_account() {
        return payment_account;
    }

    public void setPayment_account(List<PaymentBean> payment_account) {
        this.payment_account = payment_account;
    }

    public class OrderInfo implements Serializable{

        private String id;
        private String order_no;
        private String side;
        private String symbol;
        private int status;
        private String number;
        private String price;
        private String fee;
        private String remark;
        private String created_at;
        private String payment_at;
        private int payment_method;
        private String payment_bank;
        private String payment_account;
        private String payment_name;
        private String complain_at;
        private String finish_at;
        private String cancel_at;
        private int cancel_type;
        private int limit_pay_time;
        private int limit_finish_time;

        public int getLimit_pay_time() {
            return limit_pay_time;
        }

        public void setLimit_pay_time(int limit_pay_time) {
            this.limit_pay_time = limit_pay_time;
        }

        public int getLimit_finish_time() {
            return limit_finish_time;
        }

        public void setLimit_finish_time(int limit_finish_time) {
            this.limit_finish_time = limit_finish_time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrder_no() {
            return order_no;
        }

        public void setOrder_no(String order_no) {
            this.order_no = order_no;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getPayment_at() {
            return payment_at;
        }

        public void setPayment_at(String payment_at) {
            this.payment_at = payment_at;
        }

        public int getPayment_method() {
            return payment_method;
        }

        public void setPayment_method(int payment_method) {
            this.payment_method = payment_method;
        }

        public String getPayment_bank() {
            return payment_bank;
        }

        public void setPayment_bank(String payment_bank) {
            this.payment_bank = payment_bank;
        }

        public String getPayment_account() {
            return payment_account;
        }

        public void setPayment_account(String payment_account) {
            this.payment_account = payment_account;
        }

        public String getPayment_name() {
            return payment_name;
        }

        public void setPayment_name(String payment_name) {
            this.payment_name = payment_name;
        }

        public String getComplain_at() {
            return complain_at;
        }

        public void setComplain_at(String complain_at) {
            this.complain_at = complain_at;
        }

        public String getFinish_at() {
            return finish_at;
        }

        public void setFinish_at(String finish_at) {
            this.finish_at = finish_at;
        }

        public String getCancel_at() {
            return cancel_at;
        }

        public void setCancel_at(String cancel_at) {
            this.cancel_at = cancel_at;
        }

        public int getCancel_type() {
            return cancel_type;
        }

        public void setCancel_type(int cancel_type) {
            this.cancel_type = cancel_type;
        }
    }

    public class OtherInfo implements Serializable{

        private String username;
        private String name;
        private String user_id;
        private int is_star;
        private int is_black;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
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

}
