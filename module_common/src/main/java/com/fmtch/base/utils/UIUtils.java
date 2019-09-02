package com.fmtch.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fmtch.base.app.BaseApplication;


/**
 * Created by xiaxin on 2018/09/10.
 * 有关UI的工具类，如获取资源(颜色，字符串，drawable等)，
 * 屏幕宽高，dp与px转换
 */

public class UIUtils {

    private static Context getContext() {
        return BaseApplication.getApplication();
    }

    private static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取颜色值
     * @param resId 颜色资源id
     * @return 颜色值
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int getColor(int resId, Resources.Theme theme) {
        return getResources().getColor(resId, theme);
    }

    public static int getColor(String color) {
        return Color.parseColor(color);
    }

    /**
     * 获取Drawable
     * @param resTd Drawable资源id
     * @return Drawable
     */
    public static Drawable getDrawable(int resTd) {
        return getResources().getDrawable(resTd);
    }

    /**
     * 获取字符串
     * @param resId 字符串资源id
     * @return 字符串
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取字符串数组
     * @param resId 数组资源id
     * @return 字符串数组
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 将dp值转换为px值
     * @param dp 需要转换的dp值
     * @return px值
     */
    public static int dp2px(float dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * 将px值转换为dp值
     * @param px 需要转换的px值
     * @return dp值
     */
    public static int px2dp(float px) {
        return (int) (px / getResources().getDisplayMetrics().density + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics;
    }

    /**
     * 获取屏幕宽度 像素值
     * @return 屏幕宽度
     */
    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度 像素值
     * @return 屏幕高度
     */
    public static int getScreenHegith() {
        return getDisplayMetrics().heightPixels;
    }

    /**
     * 打开软键盘
     *
     * @param mEditText
     * @param mContext
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void hintKeyBoard(Activity mContext) {
        //拿到InputMethodManager
        InputMethodManager inputMethodManager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 判断当前软键盘是否打开
     *
     * @param activity
     * @return
     */
    public static boolean isSoftInputShow(Activity activity) {

        // 虚拟键盘隐藏 判断view是否为空
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            // 隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
//       inputmanger.hideSoftInputFromWindow(view.getWindowToken(),0);

            return inputmanger.isActive() && activity.getWindow().getCurrentFocus() != null;
        }
        return false;
    }

    /**
     * 获取的当前显示屏幕的高度(不包含虚拟键所占空间)
     * @param context
     * @return
     */
    public static int getDisplayScreenHeight(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        //获取的像素宽高不包含虚拟键所占空间
        DisplayMetrics metric = new DisplayMetrics();
        display.getMetrics(metric);
        return metric.heightPixels;
    }

}
