package com.fmtch.base.utils;


import com.fmtch.base.pojo.UserLoginInfo;

import io.realm.Realm;

public class InputPwdDialogUtils {

    public static final int STATUS_UN_SET_PAY_PWD = -1;

    public static final int STATUS_UN_SHOW = 0;

    public static final int STATUS_SHOW = 1;

    //是否显示输入资金密码框 -1:未设置支付密码  0：不显示  1：显示
    public static int showPwdDialog() {
        UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
        if (userLoginInfo == null)
            return STATUS_UN_SHOW;
        if (userLoginInfo.getPay_password() == 0){
            //未设置交易密码
            return STATUS_UN_SET_PAY_PWD;
        }
        switch (userLoginInfo.getPay_password_type()) {
            //交易密码验证类型: 0:每次都验证；1:不验证；2:每两小时验证一次
            case 0:
                return STATUS_SHOW;
            case 1:
                return STATUS_UN_SHOW;
            case 2:
                long lastTime = (long) SpUtils.get(KeyConstant.KEY_PAY_PWD_SUCCESS_TIME, 0L);
                long distanceTime = (System.currentTimeMillis() - lastTime) / (60 * 60 * 1000);//小时
//                long distanceTime = (System.currentTimeMillis() - lastTime) / (60 * 1000);//分钟
//                Log.e("scott", "distanceTime:" + distanceTime);
                if (distanceTime > 2)
                    return STATUS_SHOW;
                else
                    return STATUS_UN_SHOW;
            default:
                return STATUS_SHOW;
        }
    }

}
