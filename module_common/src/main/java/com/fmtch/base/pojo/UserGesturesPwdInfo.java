package com.fmtch.base.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserGesturesPwdInfo extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;//用户id

    private String gestures_pwd;//手势密码

    private boolean gestures_pwd_status;//手势密码状态 false：未开启 true：已开启

    private boolean gestures_pwd_track_status;//手势密码轨迹状态 false：未开启 true：已开启

    private boolean finger_pwd_status;//指纹密码状态 false：未开启 true：已开启

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGestures_pwd() {
        return gestures_pwd;
    }

    public void setGestures_pwd(String gestures_pwd) {
        this.gestures_pwd = gestures_pwd;
    }

    public boolean getGestures_pwd_status() {
        return gestures_pwd_status;
    }

    public void setGestures_pwd_status(boolean gestures_pwd_status) {
        this.gestures_pwd_status = gestures_pwd_status;
    }

    public boolean getGestures_pwd_track_status() {
        return gestures_pwd_track_status;
    }

    public void setGestures_pwd_track_status(boolean gestures_pwd_track_status) {
        this.gestures_pwd_track_status = gestures_pwd_track_status;
    }

    public boolean getFinger_pwd_status() {
        return finger_pwd_status;
    }

    public void setFinger_pwd_status(boolean finger_pwd_status) {
        this.finger_pwd_status = finger_pwd_status;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.gestures_pwd);
        dest.writeByte(this.gestures_pwd_status ? (byte) 1 : (byte) 0);
        dest.writeByte(this.gestures_pwd_track_status ? (byte) 1 : (byte) 0);
        dest.writeByte(this.finger_pwd_status ? (byte) 1 : (byte) 0);
    }

    public UserGesturesPwdInfo() {
    }

    protected UserGesturesPwdInfo(Parcel in) {
        this.id = in.readInt();
        this.gestures_pwd = in.readString();
        this.gestures_pwd_status = in.readByte() != 0;
        this.gestures_pwd_track_status = in.readByte() != 0;
        this.finger_pwd_status = in.readByte() != 0;
    }

    public static final Creator<UserGesturesPwdInfo> CREATOR = new Creator<UserGesturesPwdInfo>() {
        @Override
        public UserGesturesPwdInfo createFromParcel(Parcel source) {
            return new UserGesturesPwdInfo(source);
        }

        @Override
        public UserGesturesPwdInfo[] newArray(int size) {
            return new UserGesturesPwdInfo[size];
        }
    };
}
