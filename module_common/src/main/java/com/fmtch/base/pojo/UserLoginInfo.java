package com.fmtch.base.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserLoginInfo extends RealmObject implements Parcelable {
    @PrimaryKey
    private int id;//用户id
    private String pid;
    private String token;
    private String mobile;//手机号
    private String email;//邮箱
    private String avatar;//用户头像
    private String username;//用户名
    private int kyc_status;//实名认证状态: 0:未实名认证 1:已实名认证 2:审核中 3:失败
    private int ga_secret;//谷歌密钥：1-已绑定    0-未绑定
    private int pay_password;//交易密码：1-已设置   0-未设置
    private int status;//用户状态: 0:正常 1:禁用
    private int tfa_type;//二次验证类型: 0:关闭 1:手机验证 2:谷歌验证 3:二者之一
    private int pay_password_type;//交易密码验证类型: 0:每次都验证；1:不验证；2:每两小时验证一次
    private String created_at;//注册时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public int getKyc_status() {
        return kyc_status;
    }

    public void setKyc_status(int kyc_status) {
        this.kyc_status = kyc_status;
    }

    public int getGa_secret() {
        return ga_secret;
    }

    public void setGa_secret(int ga_secret) {
        this.ga_secret = ga_secret;
    }

    public int getPay_password() {
        return pay_password;
    }

    public void setPay_password(int pay_password) {
        this.pay_password = pay_password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTfa_type() {
        return tfa_type;
    }

    public void setTfa_type(int tfa_type) {
        this.tfa_type = tfa_type;
    }

    public int getPay_password_type() {
        return pay_password_type;
    }

    public void setPay_password_type(int pay_password_type) {
        this.pay_password_type = pay_password_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.token);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
        dest.writeString(this.avatar);
        dest.writeString(this.username);
        dest.writeInt(this.kyc_status);
        dest.writeInt(this.ga_secret);
        dest.writeInt(this.pay_password);
        dest.writeInt(this.status);
        dest.writeInt(this.tfa_type);
        dest.writeInt(this.pay_password_type);
        dest.writeString(this.created_at);
    }

    public UserLoginInfo() {
    }

    protected UserLoginInfo(Parcel in) {
        this.id = in.readInt();
        this.token = in.readString();
        this.mobile = in.readString();
        this.email = in.readString();
        this.avatar = in.readString();
        this.username = in.readString();
        this.kyc_status = in.readInt();
        this.ga_secret = in.readInt();
        this.pay_password = in.readInt();
        this.status = in.readInt();
        this.tfa_type = in.readInt();
        this.pay_password_type = in.readInt();
        this.created_at = in.readString();
    }

    public static final Creator<UserLoginInfo> CREATOR = new Creator<UserLoginInfo>() {
        @Override
        public UserLoginInfo createFromParcel(Parcel source) {
            return new UserLoginInfo(source);
        }

        @Override
        public UserLoginInfo[] newArray(int size) {
            return new UserLoginInfo[size];
        }
    };

    public static UserLoginInfo objectFromData(String str) {

        return new Gson().fromJson(
                str,
                UserLoginInfo.class
        );
    }
}
