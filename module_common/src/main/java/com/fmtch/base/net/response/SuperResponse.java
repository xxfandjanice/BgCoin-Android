package com.fmtch.base.net.response;

import java.io.Serializable;

public class SuperResponse implements Serializable {

    private String login_token;

    private String token;

    private String area;

    private String mobile;

    private Integer id;//用户id

    private String pid;

    private String email;//邮箱

    private String avatar;//用户头像

    private String username;//用户名

    private Integer kyc_status;//实名认证状态: 0:未实名认证 1:已实名认证 2:审核中 3:失败

    private Integer ga_secret;//谷歌密钥：1-已绑定    0-未绑定

    private Integer pay_password;//交易密码：1-已设置   0-未设置

    private Integer status;//用户状态: 0:正常 1:禁用

    private Integer tfa_type;//二次验证类型: 0:关闭 1:手机验证 2:谷歌验证 3:二者之一

    private Integer pay_password_type;//交易密码验证类型: 0:每次都验证；1:不验证；2:每两小时验证一次

    private String created_at;//注册时间

    private Integer type;

    private String secret;

    private String apk_url;

    private String version_code;

    private String version_name;

    private String upgrade_point;

    private String download_url;

    private String time;

    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getUpgrade_point() {
        return upgrade_point;
    }

    public void setUpgrade_point(String upgrade_point) {
        this.upgrade_point = upgrade_point;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getTfa_type() {
        return tfa_type;
    }

    public void setTfa_type(Integer tfa_type) {
        this.tfa_type = tfa_type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Integer getKyc_status() {
        return kyc_status;
    }

    public void setKyc_status(Integer kyc_status) {
        this.kyc_status = kyc_status;
    }

    public Integer getGa_secret() {
        return ga_secret;
    }

    public void setGa_secret(Integer ga_secret) {
        this.ga_secret = ga_secret;
    }

    public Integer getPay_password() {
        return pay_password;
    }

    public void setPay_password(Integer pay_password) {
        this.pay_password = pay_password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPay_password_type() {
        return pay_password_type;
    }

    public void setPay_password_type(Integer pay_password_type) {
        this.pay_password_type = pay_password_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
