package com.fmtch.base.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.fmtch.base.R;
import com.fmtch.base.utils.ToastUtils;


public class InputPayPwdDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private EditText etPwd;

    private OnConfirmSuccessListener mOnConfirmSuccessListener;
    private OnCancelListener mOnCancelListener;

    public InputPayPwdDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_input_pay_password, null);
    }

    public InputPayPwdDialog setOnConfirmSuccessListener(OnConfirmSuccessListener onConfirmSuccessListener) {
        this.mOnConfirmSuccessListener = onConfirmSuccessListener;
        return this;
    }


    public InputPayPwdDialog setOnCancelListener(OnCancelListener onCancelListener) {
        this.mOnCancelListener = onCancelListener;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(false);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        etPwd = mDialogLayout.findViewById(R.id.et_password);
        mDialogLayout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mOnCancelListener != null)
                    mOnCancelListener.OnCancel();
            }
        });
        mDialogLayout.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPwd.getEditableText().toString())) {
                    ToastUtils.showLongToast(R.string.please_input_pay_password);
                } else if (mOnConfirmSuccessListener != null) {
                    dialog.dismiss();
                    mOnConfirmSuccessListener.onConfirmSuccess(etPwd.getEditableText().toString());
                }
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.85); //设置宽度
//        lp.height = (int) (lp.width * 1.23); //设置高度
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    public interface OnConfirmSuccessListener {
        void onConfirmSuccess(String pay_pwd);
    }

    public interface OnCancelListener {
        void OnCancel();
    }

}
