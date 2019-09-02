package com.fmtch.module_bourse.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.utils.ConstUtils;
import com.fmtch.module_bourse.utils.TimeUtils;


public class ConfirmPayDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private TextView tvTime, tvPayWay, tvPayAccount, tvPayAmount;

    private View.OnClickListener mConfirmListener;

    private long deadTime;//截至时间
    private String payWay, payAccount, payAmount;

    private CountDownTimer countDownTimer;

    public ConfirmPayDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_confirm_pay, null);
    }

    public ConfirmPayDialog setConfirmListener(View.OnClickListener listener) {
        this.mConfirmListener = listener;
        return this;
    }

    public ConfirmPayDialog setPaymentInfo(long dead_time, String pay_way, String pay_account, String pay_amount) {
        this.deadTime = dead_time;
        this.payWay = pay_way;
        this.payAccount = pay_account;
        this.payAmount = pay_amount;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(false);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        tvTime = mDialogLayout.findViewById(R.id.tv_time);
        tvPayWay = mDialogLayout.findViewById(R.id.tv_pay_way);
        tvPayAccount = mDialogLayout.findViewById(R.id.tv_pay_account);
        tvPayAmount = mDialogLayout.findViewById(R.id.tv_pay_amount);

        //剩余时间
//        long timeSpan = TimeUtils.getTimeSpan2(deadTime, System.currentTimeMillis(), ConstUtils.TimeUnit.SEC);

        tvTime.setText(TimeUtils.getTimeSpanFormat(deadTime));
        tvPayWay.setText(payWay);
        tvPayAccount.setText(payAccount);
        tvPayAmount.setText(payAmount + " CNY");
        if (deadTime > 0 && countDownTimer == null) {
            countDownTimer = new CountDownTimer(deadTime * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvTime.setText(TimeUtils.getTimeSpanFormat(millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            };
            countDownTimer.start();
        }
        mDialogLayout.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mConfirmListener != null)
                    mConfirmListener.onClick(v);
            }
        });
        mDialogLayout.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager windowManager = dialogWindow.getWindowManager();
        dialogWindow.setWindowAnimations(R.style.DialogInAndOutAnim);
        dialogWindow.setGravity(Gravity.CENTER);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.83); //设置宽度
        dialogWindow.setAttributes(lp);
        return dialog;
    }


}
