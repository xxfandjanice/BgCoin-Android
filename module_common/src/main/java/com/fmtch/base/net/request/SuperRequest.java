package com.fmtch.base.net.request;

import java.io.Serializable;

public class SuperRequest implements Serializable {

    private String mobile;
    private String email;
    private String area;
    private String login_password;
    private String sms_code;
    private String email_code;
    private String google_code;
    private String parent_id;
    private String login_name;
    private String login_token;
    private Integer tfa_type;
    private String passport_type;
    private String country;
    private String name;
    private String passport_id;
    private String passport_front;
    private String passport_back;
    private String passport_image;
    private String username;
    private String new_login_password;
    private String pay_password;
    private String new_pay_password;
    private String pay_password_type;
    private String google_secret;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGoogle_secret() {
        return google_secret;
    }

    public void setGoogle_secret(String google_secret) {
        this.google_secret = google_secret;
    }

    public String getNew_pay_password() {
        return new_pay_password;
    }

    public void setNew_pay_password(String new_pay_password) {
        this.new_pay_password = new_pay_password;
    }

    public String getPay_password() {
        return pay_password;
    }

    public void setPay_password(String pay_password) {
        this.pay_password = pay_password;
    }

    public String getPay_password_type() {
        return pay_password_type;
    }

    public void setPay_password_type(String pay_password_type) {
        this.pay_password_type = pay_password_type;
    }

    public String getNew_login_password() {
        return new_login_password;
    }

    public void setNew_login_password(String new_login_password) {
        this.new_login_password = new_login_password;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getEmail_code() {
        return email_code;
    }

    public void setEmail_code(String email_code) {
        this.email_code = email_code;
    }

    public String getGoogle_code() {
        return google_code;
    }

    public void setGoogle_code(String google_code) {
        this.google_code = google_code;
    }


    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLogin_password() {
        return login_password;
    }

    public void setLogin_password(String login_password) {
        this.login_password = login_password;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }

    public Integer getTfa_type() {
        return tfa_type;
    }

    public void setTfa_type(Integer tfa_type) {
        this.tfa_type = tfa_type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassport_id() {
        return passport_id;
    }

    public void setPassport_id(String passport_id) {
        this.passport_id = passport_id;
    }

    public String getPassport_front() {
        return passport_front;
    }

    public void setPassport_front(String passport_front) {
        this.passport_front = passport_front;
    }

    public String getPassport_back() {
        return passport_back;
    }

    public void setPassport_back(String passport_back) {
        this.passport_back = passport_back;
    }

    public String getPassport_image() {
        return passport_image;
    }

    public void setPassport_image(String passport_image) {
        this.passport_image = passport_image;
    }

    public String getPassport_type() {
        return passport_type;
    }

    public void setPassport_type(String passport_type) {
        this.passport_type = passport_type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
