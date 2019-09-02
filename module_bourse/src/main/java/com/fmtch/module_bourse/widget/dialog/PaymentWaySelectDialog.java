package com.fmtch.module_bourse.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.PaymentBean;

import java.util.ArrayList;
import java.util.List;


public class PaymentWaySelectDialog {

    private Context mContext;                       //上下文
    private View mDialogLayout;                     //弹窗布局
    private Dialog dialog;                          //构建的弹窗

    private List<PaymentBean> mPayments;

    private int pos;//0:银行卡   1：支付宝  2：微信
    private SelectPayWayListener mSelectPayWayListener;

    public PaymentWaySelectDialog(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialogLayout = inflater.inflate(R.layout.dialog_select_pay_way, null);
    }

    public PaymentWaySelectDialog setSelectPos(int pos) {
        this.pos = pos;
        return this;
    }

    public PaymentWaySelectDialog setPayments(List<PaymentBean> payments) {
        this.mPayments = payments;
        return this;
    }

    public PaymentWaySelectDialog setSelectPayWayListener(SelectPayWayListener listener) {
        this.mSelectPayWayListener = listener;
        return this;
    }

    public Dialog builder() {
        dialog = new Dialog(mContext, R.style.MyDialogTheme);
        dialog.setCancelable(false);
        dialog.addContentView(mDialogLayout, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        TextView tvfirst = mDialogLayout.findViewById(R.id.tv_first);
        TextView tvSecond = mDialogLayout.findViewById(R.id.tv_second);
        TextView tvThird = mDialogLayout.findViewById(R.id.tv_third);
        if (mPayments != null && mPayments.size() > 0) {
            tvfirst.setVisibility(View.GONE);
            tvSecond.setVisibility(View.GONE);
            tvThird.setVisibility(View.GONE);
            for (PaymentBean paymentBean : mPayments) {
                //0:银行卡   1：支付宝  2：微信
                switch (paymentBean.getType()) {
                    case 0:
                        tvfirst.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tvSecond.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        tvThird.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        //0:银行卡   1：支付宝  2：微信
        switch (pos) {
            case 0:
                tvfirst.setBackgroundResource(R.color.cl_f2f6fb);
                tvSecond.setBackgroundResource(R.color.white);
                tvThird.setBackgroundResource(R.color.white);
                tvfirst.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_bank_card, 0, R.drawable.icon_checked, 0);
                tvSecond.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_zfb, 0, 0, 0);
                tvThird.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_wechat, 0, 0, 0);
                break;
            case 1:
                tvfirst.setBackgroundResource(R.color.white);
                tvSecond.setBackgroundResource(R.color.cl_f2f6fb);
                tvThird.setBackgroundResource(R.color.white);
                tvfirst.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_bank_card, 0, 0, 0);
                tvSecond.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_zfb, 0, R.drawable.icon_checked, 0);
                tvThird.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_wechat, 0, 0, 0);
                break;
            case 2:
                tvfirst.setBackgroundResource(R.color.white);
                tvSecond.setBackgroundResource(R.color.white);
                tvThird.setBackgroundResource(R.color.cl_f2f6fb);
                tvfirst.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_bank_card, 0, 0, 0);
                tvSecond.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_zfb, 0, 0, 0);
                tvThird.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_wechat, 0, R.drawable.icon_checked, 0);
                break;
        }
        tvfirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSelectPayWayListener != null) {
                    if(mPayments != null && mPayments.size() > 0){
                        for (PaymentBean paymentBean : mPayments){
                            if (paymentBean.getType() == 0){
                                mSelectPayWayListener.selectPayment(0,paymentBean);
                                return;
                            }
                        }
                    }else {
                        mSelectPayWayListener.selectPayment(0,null);
                    }
                }
            }
        });
        tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSelectPayWayListener != null) {
                    if(mPayments != null && mPayments.size() > 0){
                        for (PaymentBean paymentBean : mPayments){
                            if (paymentBean.getType() == 1){
                                mSelectPayWayListener.selectPayment(1,paymentBean);
                                return;
                            }
                        }
                    }else {
                        mSelectPayWayListener.selectPayment(1,null);
                    }
                }
            }
        });
        tvThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSelectPayWayListener != null) {
                    if(mPayments != null && mPayments.size() > 0){
                        for (PaymentBean paymentBean : mPayments){
                            if (paymentBean.getType() == 2){
                                mSelectPayWayListener.selectPayment(2,paymentBean);
                                return;
                            }
                        }
                    }else {
                        mSelectPayWayListener.selectPayment(2,null);
                    }
                }
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
        dialogWindow.setWindowAnimations(R.style.BottomDialogInAndOutAnim);
        dialogWindow.setGravity(Gravity.BOTTOM);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = display.getWidth(); //设置宽度
        dialogWindow.setAttributes(lp);
        return dialog;
    }

    public interface SelectPayWayListener {
        void selectPayment(int type,PaymentBean paymentBean);
    }

}
