package com.fmtch.base.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

import com.fmtch.base.app.BaseApplication;


/**
 * Created by pocketEos on 2017/11/23.
 */
public final class ToastUtils {


    private static Handler handler = new Handler(Looper.getMainLooper());

    private static Toast toast = null;

    private static Object synObj = new Object();

    public static void showLongToast(String msg){
        showMessage(msg, Toast.LENGTH_LONG);
    }

    public static void showLongToast(@StringRes int msg){
        showMessage(msg, Toast.LENGTH_LONG);
    }

    public static void showMessage(String msg) {

        showMessage(
                msg,
                Toast.LENGTH_SHORT
        );
    }

    public static void showMessage(@StringRes int msg) {

        showMessage(
                msg,
                Toast.LENGTH_SHORT
        );
    }

    public static void showMessageCenter(final String msg) {

        toast = Toast.makeText(
                BaseApplication.getApplication(),
                msg,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(
                Gravity.CENTER,
                0,
                0
        );
        toast.show();
    }




    public static void showMessage(final String msg, final int len) {
        new Thread(new Runnable() {

            public void run() {

                handler.post(new Runnable() {

                    @Override
                    public void run() {

                        synchronized (synObj) {

                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(
                                    BaseApplication.getApplication(),
                                    msg,
                                    len
                            );
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }


    public static void showMessage(final int msg, final int len) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(
                                    BaseApplication.getApplication(),
                                    msg,
                                    len
                            );
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    public static long LAST_CLOCK_TIME;

    // 防误点
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - LAST_CLOCK_TIME < 300) {
            return true;
        }
        LAST_CLOCK_TIME = time;
        return false;
    }

}