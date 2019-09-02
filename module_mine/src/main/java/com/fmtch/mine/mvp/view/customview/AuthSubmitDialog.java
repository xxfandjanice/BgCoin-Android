package com.fmtch.mine.mvp.view.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fmtch.mine.R;


public class AuthSubmitDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗
    private boolean mCancelable = true;//是否弹窗可关闭（默认可关闭）

    private View.OnClickListener mOnClickListener;

    public AuthSubmitDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.mine_dialog_auth_submit, null);
    }

    public AuthSubmitDialog setOnOkClickListener(View.OnClickListener listener){
        this.mOnClickListener = listener;
        return this;
    }

    //设置不可关闭时为强制更新不可点击取消
    public AuthSubmitDialog setCancelable(boolean cancel) {
        this.mCancelable = cancel;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(mCancelable);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        mDialogLayout.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mOnClickListener.onClick(v);
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.9); //设置宽度
//        lp.height = (int) (lp.width * 1.23); //设置高度
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

}
