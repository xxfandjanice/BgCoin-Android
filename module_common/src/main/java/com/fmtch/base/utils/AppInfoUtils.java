package com.fmtch.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoUtils {
    /**
     * 获取版本名
     * @param context  当前上下文
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {
            return versionName;
        }
    }

    /**
     * 获取版本号
     * @param context  当前上下文
     * @return
     */
    public static int getVersionNo(Context context) {
        PackageManager manager = context.getPackageManager();
        int versionName = 1;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }finally {
            return versionName;
        }
    }
}
