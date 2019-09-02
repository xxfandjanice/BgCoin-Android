package com.fmtch.base.widget.dialog;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fmtch.base.R;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.utils.TimeCount;
import com.fmtch.base.utils.ToastUtils;


public class SecondCheckDialog implements View.OnClickListener {


    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗
    private boolean mCancelable = false;//是否弹窗可关闭（默认可关闭）

    private int mTyfType; //二次验证类型: 0:关闭 1:手机验证 2:邮箱验证 3:手机+谷歌 4:邮箱+谷歌 5:手机+邮箱+谷歌 6:手机+邮箱

    private LinearLayout mLLSmsCode, mLLEmaliCode, mLLGoogleCode;
    private EditText mEtSmsCode, mEtEmaliCode, mEtGoogleCode;
    private TextView mTvGetSmsCode, mTvGetEmaliCode;

    private OnCancleListener mOnCancleListener;
    private OnConfirmListener mOnConfirmListener;

    private TimeCount mSmsTimeCount;
    private TimeCount mEmailTimeCount;

    private String mobile, area,account;

    public SecondCheckDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_second_check, null);
    }

    public SecondCheckDialog setTyfType(int tfa_type) {
        this.mTyfType = tfa_type;
        return this;
    }

    public SecondCheckDialog setAccount(String account) {
        this.account = account;
        return this;
    }

    public SecondCheckDialog setMobileInfo(String mobile, String area) {
        this.mobile = mobile;
        this.area = area;
        return this;
    }

    public SecondCheckDialog setCancleListener(OnCancleListener onCancleListener) {
        this.mOnCancleListener = onCancleListener;
        return this;
    }

    public SecondCheckDialog setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.mOnConfirmListener = onConfirmListener;
        return this;
    }

    //设置不可关闭时为强制更新不可点击取消
    public SecondCheckDialog setCancelable(boolean cancel) {
        this.mCancelable = cancel;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(mCancelable);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogInAndOutAnim);
        mLLSmsCode = mDialogLayout.findViewById(R.id.ll_sms_code);
        mLLEmaliCode = mDialogLayout.findViewById(R.id.ll_email_code);
        mLLGoogleCode = mDialogLayout.findViewById(R.id.ll_google_code);
        mEtSmsCode = mDialogLayout.findViewById(R.id.et_sms_code);
        mEtEmaliCode = mDialogLayout.findViewById(R.id.et_email_code);
        mEtGoogleCode = mDialogLayout.findViewById(R.id.et_google_code);
        mTvGetSmsCode = mDialogLayout.findViewById(R.id.tv_sms_get_code);
        mTvGetEmaliCode = mDialogLayout.findViewById(R.id.tv_email_get_code);
        mDialogLayout.findViewById(R.id.iv_close).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.tv_sms_get_code).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.tv_email_get_code).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.iv_google_code_copy).setOnClickListener(this);
        mDialogLayout.findViewById(R.id.tv_ok).setOnClickListener(this);
        //二次验证类型: 0:关闭 1:手机验证 2:邮箱验证 3:手机+谷歌 4:邮箱+谷歌 5:手机+邮箱+谷歌 6:手机+邮箱 7:谷歌验证(仅用于登录二次校验)
        switch (mTyfType) {
            case 0:
                dialog.dismiss();
                if (mOnConfirmListener != null)
                    mOnConfirmListener.onConfirm(new SuperRequest());
                break;
            case 1:
                mLLSmsCode.setVisibility(View.VISIBLE);
                break;
            case 2:
                mLLEmaliCode.setVisibility(View.VISIBLE);
                break;
            case 3:
                mLLSmsCode.setVisibility(View.VISIBLE);
                mLLGoogleCode.setVisibility(View.VISIBLE);
                break;
            case 4:
                mLLEmaliCode.setVisibility(View.VISIBLE);
                mLLGoogleCode.setVisibility(View.VISIBLE);
                break;
            case 5:
                mLLSmsCode.setVisibility(View.VISIBLE);
                mLLEmaliCode.setVisibility(View.VISIBLE);
                mLLGoogleCode.setVisibility(View.VISIBLE);
                break;
            case 6:
                mLLSmsCode.setVisibility(View.VISIBLE);
                mLLEmaliCode.setVisibility(View.VISIBLE);
                break;
            case 7:
                mLLGoogleCode.setVisibility(View.VISIBLE);
                break;
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mSmsTimeCount != null)
                    mSmsTimeCount.cancel();
                if (mEmailTimeCount != null)
                    mEmailTimeCount.cancel();
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

    @Override
    public void onClick(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.iv_close) {
            dialog.dismiss();
            if (mOnCancleListener != null)
                mOnCancleListener.onCancle();
        } else if (id == R.id.tv_sms_get_code) {
            //获取短信验证码
            String api = API.SEND_SMS_CODE;
            if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(area)) {
                api = api + "?area=" + area + "&mobile=" + mobile;
            }else if (!TextUtils.isEmpty(account)){
                api = api + "?area=86&mobile=" + account;
            }
            RequestUtil.requestGet(api, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    ToastUtils.showLongToast(R.string.send_success);
                    if (mSmsTimeCount == null)
                        mSmsTimeCount = new TimeCount(60000, 1000, mTvGetSmsCode);
                    mSmsTimeCount.start();
                }
            }, mContext, true, mContext.getResources().getString(R.string.loading_checking));
        } else if (id == R.id.tv_email_get_code) {
            //获取邮箱验证码
            String api = API.SEND_EMAIL_CODE;
            if (!TextUtils.isEmpty(account)){
                api = api + "?email=" + account;
            }
            RequestUtil.requestGet(api, new OnResponseListenerImpl() {
                @Override
                public void onNext(SuperResponse response) {
                    super.onNext(response);
                    ToastUtils.showLongToast(R.string.send_success);
                    if (mEmailTimeCount == null)
                        mEmailTimeCount = new TimeCount(60000, 1000, mTvGetEmaliCode);
                    mEmailTimeCount.start();
                }
            }, mContext, true, mContext.getResources().getString(R.string.loading_checking));

        } else if (id == R.id.iv_google_code_copy) {
            //复制google验证码
            String google_code = mEtGoogleCode.getEditableText().toString();
            if (TextUtils.isEmpty(google_code)) {
                ToastUtils.showLongToast(R.string.please_input_sure_google_code);
            } else {
                ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(google_code);
                ToastUtils.showLongToast(mContext.getResources().getString(R.string.copy_success_) + google_code);
            }
        } else if (id == R.id.tv_ok) {
            SuperRequest superRequest = new SuperRequest();
            String sms_code = mEtSmsCode.getEditableText().toString();
            String email_code = mEtEmaliCode.getEditableText().toString();
            String google_code = mEtGoogleCode.getEditableText().toString();
            if (mLLSmsCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(sms_code) && sms_code.length() != 6) {
                ToastUtils.showLongToast(R.string.please_input_sure_sms_code);
                return;
            } else {
                superRequest.setSms_code(sms_code);
            }
            if (mLLEmaliCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(email_code) && email_code.length() != 6) {
                ToastUtils.showLongToast(R.string.please_input_sure_email_code);
                return;
            } else {
                superRequest.setEmail_code(email_code);
            }
            if (mLLGoogleCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(google_code) && google_code.length() != 6) {
                ToastUtils.showLongToast(R.string.please_input_sure_google_code);
                return;
            } else {
                superRequest.setGoogle_code(google_code);
            }
            dialog.dismiss();
            if (mOnConfirmListener != null)
                mOnConfirmListener.onConfirm(superRequest);
        }
    }

    public interface OnCancleListener {
        void onCancle();
    }

    public interface OnConfirmListener {
        void onConfirm(SuperRequest request);
    }


}
