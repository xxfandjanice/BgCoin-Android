package com.fmtch.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fmtch.base.R;


/**
 * 加载动画
 */
public class ShowDialog extends Dialog {
    public static ShowDialog dialog;
    public static TextView txt;
    private static ProgressBar imageView;
    private static ImageView ivSuccess;

    public ShowDialog(@NonNull Context context) {
        super(context);
    }

    public ShowDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public ShowDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * 当窗口焦点改变时调用
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    /**
     * 给Dialog设置提示信息
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            txt.setVisibility(View.VISIBLE);
            txt.setText(message);
            txt.invalidate();
        }
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context        上下文
     * @param message        提示
     * @param cancelable     是否按返回键取消
     * @param cancelListener 按下返回键监听
     * @return
     */
    public synchronized static ShowDialog showDialog(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        dialog = new ShowDialog(context, R.style.Custom_Progress);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_dialog);
        if (TextUtils.isEmpty(message)) {
            dialog.findViewById(R.id.tv_message).setVisibility(View.GONE);
        } else {
            txt =dialog.findViewById(R.id.tv_message);
            txt.setText(message);
        }
        // 设置点击屏幕Dialog不消失
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);//设置为false，按返回键不能退出。默认为true。
        // 监听返回键处理
        dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        // 设置背景层透明度
        lp.dimAmount = 0.1f;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public synchronized static ShowDialog showMyDialog(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        dialog = new ShowDialog(context, R.style.My_Custom_Progress);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_dialog);
        if (TextUtils.isEmpty(message)) {
            dialog.findViewById(R.id.tv_message).setVisibility(View.GONE);
        } else {
            txt =dialog.findViewById(R.id.tv_message);
            txt.setText(message);
        }
        // 设置点击屏幕Dialog不消失
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);//设置为false，按返回键不能退出。默认为true。
        // 监听返回键处理
        dialog.setOnCancelListener(cancelListener);
        // 设置居中
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
        return dialog;
    }

    public synchronized static void dissmiss() {
        if (null != dialog) {
            dialog.dismiss();
        }
    }

    public static void setMessage(String message) {
        if (null != txt) {
            txt.setText(message);
        }
    }

}
