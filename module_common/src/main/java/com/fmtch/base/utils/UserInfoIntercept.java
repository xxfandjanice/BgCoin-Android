package com.fmtch.base.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.R;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.widget.dialog.CommonDialog;

import io.realm.Realm;

public class UserInfoIntercept {

    //判断用户是否实名认证
    public static boolean userAuth(Context context){
        UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (userLoginInfo == null) {
            //未登录跳转登录页
            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
        } else if (userLoginInfo.getKyc_status() == 0) {
            //未实名认证
            CommonDialog dialog = new CommonDialog(context);
            dialog.showMsg(context.getString(R.string.no_auth), false);
            dialog.setBtnConfirmText(context.getString(R.string.go_auth));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.AUTH).navigation();
                }
            });
        } else if (userLoginInfo.getKyc_status() == 1) {
            //已经实名认证
            return true;
        } else {
            //申请实名认证进入实名认证状态
            ARouter.getInstance().build(RouterMap.AUTH_STATUS).navigation();
        }
        return false;
    }

    //判断用户是否绑定手机号
    public static boolean userBindMobile(Activity activity){
        UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (userLoginInfo == null) {
            //未登录跳转登录页
            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
        }else if (TextUtils.isEmpty(userLoginInfo.getMobile())){
            //绑定手机号
            CommonDialog dialog = new CommonDialog(activity);
            dialog.showMsg(activity.getString(R.string.no_bind_mobile), false);
            dialog.setBtnConfirmText(activity.getString(R.string.go_bind));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.BIND).navigation();
                }
            });
        }else {
            return true;
        }
        return false;
    }

    //判断用户是否设置用户名
    public static boolean haveUserName(Activity activity){
        UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (userLoginInfo == null) {
            //未登录跳转登录页
            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
        }else if (TextUtils.isEmpty(userLoginInfo.getUsername())){
            //设置用户名
            CommonDialog dialog = new CommonDialog(activity);
            dialog.showMsg(activity.getString(R.string.no_set_username), false);
            dialog.setBtnConfirmText(activity.getString(R.string.go_set));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.SET_USER_NAME).navigation();
                }
            });
        }else {
            return true;
        }
        return false;
    }

    //判断用户是否设置资金密码
    public static boolean havePayPwd(Activity activity){
        UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (userLoginInfo == null) {
            //未登录跳转登录页
            ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
        }else if (userLoginInfo.getPay_password() != 1){
            //设置资金密码
            CommonDialog dialog = new CommonDialog(activity);
            dialog.showMsg(activity.getString(R.string.no_set_pay_pwd), false);
            dialog.setBtnConfirmText(activity.getString(R.string.go_set));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.SET_ASSETS_PWD)
                            .withBoolean("is_modify", false)
                            .navigation();
                }
            });
        }else {
            return true;
        }
        return false;
    }

    //判断用户是否设置收付款方式
    public static boolean havePayment(Activity activity) {
        boolean havePayment = (boolean) SpUtils.get(KeyConstant.KEY_PAYMENT_STATUS, false);
        if (!havePayment) {
            //设置收付款方式
            CommonDialog dialog = new CommonDialog(activity);
            dialog.showMsg(activity.getString(R.string.no_set_payment), false);
            dialog.setBtnConfirmText(activity.getString(R.string.go_set));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.PAYMENT_SETTING).navigation();
                }
            });
        }
        return havePayment;
    }

}
