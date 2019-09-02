package com.fmtch.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fmtch.base.R;


public class CustomDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private View.OnClickListener mCancelListener;
    private View.OnClickListener mSubmitListener;

    private double mWidthFloat = 0.83;//默认占屏幕宽
    private boolean mCancelable = true;//默认可关闭
    private final FrameLayout contentLayout;

    public CustomDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_custom, null);
        contentLayout = mDialogLayout.findViewById(R.id.fl_content);
    }

    public CustomDialog setTitle(String title) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_title)).setText(title);
        return this;
    }

    public CustomDialog setTitle(int titleRes) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_title)).setText(titleRes);
        return this;
    }

    public CustomDialog setContent(String content) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_content)).setText(content);
        return this;
    }

    public CustomDialog setContent(int contentRes) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_content)).setText(contentRes);
        return this;
    }

    public CustomDialog setLeftBtnStr(String str) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_cancel)).setText(str);
        return this;
    }

    public CustomDialog setLeftBtnStr(int strRes) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_cancel)).setText(strRes);
        return this;
    }

    public CustomDialog setRightBtnStr(String str) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_submit)).setText(str);
        return this;
    }

    public CustomDialog setRightBtnStr(int strRes) {
        ((TextView) mDialogLayout.findViewById(R.id.tv_submit)).setText(strRes);
        return this;
    }

    public CustomDialog setCancelListener(View.OnClickListener listener) {
        this.mCancelListener = listener;
        return this;
    }

    public CustomDialog setSubmitListener(View.OnClickListener listener) {
        this.mSubmitListener = listener;
        return this;
    }

    public CustomDialog setDialogWidthFloat(double width) {
        this.mWidthFloat = width;
        return this;
    }

    public CustomDialog setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        return this;
    }

    public CustomDialog setContentView(View contentView) {
        this.contentLayout.removeAllViews();
        this.contentLayout.addView(contentView);
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(mCancelable);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        mDialogLayout.findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSubmitListener != null)
                    mSubmitListener.onClick(v);
            }
        });
        mDialogLayout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mCancelListener != null)
                    mCancelListener.onClick(v);
            }
        });
        mDialogLayout.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelListener != null)
                    mCancelListener.onClick(v);
                dialog.dismiss();
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        dialogWindow.setWindowAnimations(R.style.DialogInAndOutAnim);
        dialogWindow.setGravity(Gravity.CENTER);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * mWidthFloat); //设置宽度
        dialogWindow.setAttributes(lp);
        return dialog;
    }


}
