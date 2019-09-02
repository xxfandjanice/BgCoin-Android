package com.fmtch.module_bourse.bean;

import java.io.Serializable;

/**
 * Created by wtc on 2019/5/21
 */
public class AccountDetailBean implements Serializable {

    /**
     * id : 7
     * user_id : 1
     * action_id : 1
     * coin_id : 1
     * number : 31.00000000
     * type : 31
     * type_text : 转入
     * available_before : 0.00000000
     * disabled_before : 0.00000000
     * available_after : 31.00000000
     * disabled_after : 0.00000000
     * time : 2019-05-20 19:20:29
     * status : 已完成
     * detail : {"sign":"+","type":31,"type_text":"转入","type_tip":"币币账户到我的钱包","status":"已完成","hash":"","time":"2019-05-20 19:20:29"}
     */

    private int id;
    private int user_id;
    private int action_id;
    private int coin_id;
    private String number;
    private int type;
    private String type_text;
    private String available_before;
    private String disabled_before;
    private String available_after;
    private String disabled_after;
    private String time;
    private String status;
    private DetailBean detail;
    private String op_name;
    private String created_at;
    private String available_number;
    private String disabled_number;
    private String from_account;
    private String to_account;
    private String sign;
    private String hash;
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFrom_account() {
        return from_account;
    }

    public void setFrom_account(String from_account) {
        this.from_account = from_account;
    }

    public String getTo_account() {
        return to_account;
    }

    public void setTo_account(String to_account) {
        this.to_account = to_account;
    }

    public String getAvailable_number() {
        return available_number;
    }

    public void setAvailable_number(String available_number) {
        this.available_number = available_number;
    }

    public String getDisabled_number() {
        return disabled_number;
    }

    public void setDisabled_number(String disabled_number) {
        this.disabled_number = disabled_number;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getOp_name() {
        return op_name;
    }

    public void setOp_name(String op_name) {
        this.op_name = op_name;
    }

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

    public int getAction_id() {
        return action_id;
    }

    public void setAction_id(int action_id) {
        this.action_id = action_id;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getType_text() {
        return type_text;
    }

    public void setType_text(String type_text) {
        this.type_text = type_text;
    }

    public String getAvailable_before() {
        return available_before;
    }

    public void setAvailable_before(String available_before) {
        this.available_before = available_before;
    }

    public String getDisabled_before() {
        return disabled_before;
    }

    public void setDisabled_before(String disabled_before) {
        this.disabled_before = disabled_before;
    }

    public String getAvailable_after() {
        return available_after;
    }

    public void setAvailable_after(String available_after) {
        this.available_after = available_after;
    }

    public String getDisabled_after() {
        return disabled_after;
    }

    public void setDisabled_after(String disabled_after) {
        this.disabled_after = disabled_after;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DetailBean getDetail() {
        return detail;
    }

    public void setDetail(DetailBean detail) {
        this.detail = detail;
    }

    public static class DetailBean implements Serializable{
        /**
         * sign : +
         * type : 31
         * type_text : 转入
         * type_tip : 币币账户到我的钱包
         * status : 已完成
         * hash :
         * time : 2019-05-20 19:20:29
         */

        private String sign;
        private int type;
        private String type_text;
        private String type_tip;
        private String status;
        private String hash;
        private String time;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getType_text() {
            return type_text;
        }

        public void setType_text(String type_text) {
            this.type_text = type_text;
        }

        public String getType_tip() {
            return type_tip;
        }

        public void setType_tip(String type_tip) {
            this.type_tip = type_tip;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
