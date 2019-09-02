package com.fmtch.module_bourse.utils;

import android.text.TextUtils;

import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;

/**
 * Created by wtc on 2019/5/29
 */
public class AppUtils {

    public static boolean isLogin() {
        String token = (String) SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, "");
        return !TextUtils.isEmpty(token);
    }
}
