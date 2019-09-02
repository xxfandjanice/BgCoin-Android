package com.fmtch.base.utils;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.pojo.UserGesturesPwdInfo;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.ArouterNavCallback;
import com.fmtch.base.router.RouterMap;

import io.realm.Realm;

public class GesturesFingerPwdUtils {

    //校验是否进入验证页
    public static void Check2GesturesOrFinger(Activity activity,String router) {
        Realm mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            UserGesturesPwdInfo mUserGesturesPwdInfo = mRealm.where(UserGesturesPwdInfo.class)
                    .equalTo("id", mUserLoginInfo.getId())
                    .findFirst();
            if (mUserGesturesPwdInfo != null && mUserGesturesPwdInfo.getGestures_pwd_status() && !StringUtils.isEmpty(mUserGesturesPwdInfo.getGestures_pwd())) {
                //手势密码
                ARouter.getInstance().build(RouterMap.CHECK_GESTURES_PWD)
                        .withString("router",router)
                        .navigation(activity, new ArouterNavCallback(activity));
                return;
            } else if (mUserGesturesPwdInfo != null && mUserGesturesPwdInfo.getFinger_pwd_status()) {
                //指纹密码
                ARouter.getInstance().build(RouterMap.CHECK_FINGER_PWD)
                        .withString("router",router)
                        .navigation(activity, new ArouterNavCallback(activity));
                return;
            }
        }
        ARouter.getInstance().build(RouterMap.MAIN_PAGE).navigation(activity, new ArouterNavCallback(activity));
    }

    //获取手势密码
    public static String getGesturesPwd() {
        String gestures_pwd = "";
        Realm mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            UserGesturesPwdInfo mUserGesturesPwdInfo = mRealm.where(UserGesturesPwdInfo.class)
                    .equalTo("id", mUserLoginInfo.getId())
                    .findFirst();
            if (mUserGesturesPwdInfo != null && !StringUtils.isEmpty(mUserGesturesPwdInfo.getGestures_pwd())) {
                gestures_pwd = mUserGesturesPwdInfo.getGestures_pwd();
            }
        }
        return gestures_pwd;
    }

    //获取手势密码轨迹是否打开
    public static boolean getGesturesTrackIsOpen() {
        boolean gestures_open = false;
        Realm mRealm = Realm.getDefaultInstance();
        UserLoginInfo mUserLoginInfo = mRealm.where(UserLoginInfo.class).findFirst();
        if (mUserLoginInfo != null) {
            UserGesturesPwdInfo mUserGesturesPwdInfo = mRealm.where(UserGesturesPwdInfo.class)
                    .equalTo("id", mUserLoginInfo.getId())
                    .findFirst();
            if (mUserGesturesPwdInfo != null) {
                gestures_open = mUserGesturesPwdInfo.getGestures_pwd_track_status();
            }
        }
        return gestures_open;
    }

}
