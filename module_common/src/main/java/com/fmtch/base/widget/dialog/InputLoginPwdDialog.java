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
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.utils.ToastUtils;


public class InputLoginPwdDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private EditText etPwd;

    private OnConfirmSuccessListener mOnConfirmSuccessListener;
    private OnCancelListener mOnCancelListener;
    private OnConfirmFailListener mOnConfirmFailListener;

    public InputLoginPwdDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_input_login_password, null);
    }

    public InputLoginPwdDialog setOnConfirmSuccessListener(OnConfirmSuccessListener onConfirmSuccessListener) {
        this.mOnConfirmSuccessListener = onConfirmSuccessListener;
        return this;
    }

    public InputLoginPwdDialog setOnConfirmFailListener(OnConfirmFailListener onConfirmFailListener) {
        this.mOnConfirmFailListener = onConfirmFailListener;
        return this;
    }

    public InputLoginPwdDialog setOnCancelListener(OnCancelListener onCancelListener) {
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
                    ToastUtils.showLongToast(R.string.please_input_login_password);
                } else if (mOnConfirmSuccessListener != null) {
                    SuperRequest superRequest = new SuperRequest();
                    superRequest.setLogin_password(etPwd.getEditableText().toString());
                    RequestUtil.requestPost(API.CHECK_LOGIN_PWD, superRequest, new OnResponseListenerImpl() {
                        @Override
                        public void onNext(SuperResponse response) {
                            super.onNext(response);
                            if (response.getType() == 1) {
                                //密码正确
                                dialog.dismiss();
                                mOnConfirmSuccessListener.onConfirmSuccess();
                            } else {
                                //密码错误
                                ToastUtils.showLongToast(R.string.input_login_pass_word_error);
                                if (mOnConfirmFailListener != null){
                                    dialog.dismiss();
                                    mOnConfirmFailListener.onConfirmFail();
                                }
                            }
                        }

                    }, mContext, true, mContext.getResources().getString(R.string.loading_checking));
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
        void onConfirmSuccess();
    }

    public interface OnConfirmFailListener {
        void onConfirmFail();
    }

    public interface OnCancelListener {
        void OnCancel();
    }

}
