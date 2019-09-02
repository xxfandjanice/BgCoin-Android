package com.fmtch.module_bourse.ui.trade.activity;


import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.StatusBarUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.WaitingOpenDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.trade.model.ParisBuyOrderModel;
import com.fmtch.module_bourse.widget.dialog.SaveReceivePicDialog;


import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.PARIS_BUY_ORDER, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ParisBuyOrderActivity extends BaseActivity {


    @BindView(R2.id.tv_title)
    public TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_order_no)
    public TextView tvOrderNo;
    @BindView(R2.id.tv_order_status)
    public TextView tvOrderStatus;
    @BindView(R2.id.tv_user_name)
    public TextView tvUserName;
    @BindView(R2.id.tv_order_money)
    public TextView tvOrderMoney;
    @BindView(R2.id.tv_order_num)
    public TextView tvOrderNum;
    @BindView(R2.id.tv_single_price)
    public TextView tvSinglePrice;
    @BindView(R2.id.tv_order_time)
    public TextView tvOrderTime;
    @BindView(R2.id.tv_pay_way)
    public TextView tvPayWay;
    @BindView(R2.id.tv_bank_name)
    public TextView tvBankName;
    @BindView(R2.id.tv_bank_receiver)
    public TextView tvBankReceiver;
    @BindView(R2.id.tv_bank_card_no)
    public TextView tvBankCardNo;
    @BindView(R2.id.tv_bank_belong)
    public TextView tvBankBelong;
    @BindView(R2.id.ll_bank)
    public LinearLayout llBank;
    @BindView(R2.id.tv_zfb_receiver)
    public TextView tvZfbReceiver;
    @BindView(R2.id.tv_zfb_account)
    public TextView tvZfbAccount;
    @BindView(R2.id.iv_zfb_receive_pic)
    public ImageView ivZfbReceivePic;
    @BindView(R2.id.ll_zfb)
    public LinearLayout llZfb;
    @BindView(R2.id.tv_wechat_receiver)
    public TextView tvWechatReceiver;
    @BindView(R2.id.tv_wechat_account)
    public TextView tvWechatAccount;
    @BindView(R2.id.iv_wechat_receive_pic)
    public ImageView ivWechatReceivePic;
    @BindView(R2.id.ll_wechat)
    public LinearLayout llWechat;
    @BindView(R2.id.tv_chat_online)
    public TextView tvChatOnline;
    @BindView(R2.id.tv_confirm)
    public TextView tvConfirm;
    @BindView(R2.id.tv_status_third)
    public TextView tvStatusThird;
    @BindView(R2.id.iv_status_third)
    public ImageView ivStatusThird;
    @BindView(R2.id.tv_status_four)
    public TextView tvStatusFour;
    @BindView(R2.id.iv_status_four)
    public ImageView ivStatusFour;
    @BindView(R2.id.tv_order_explain)
    public TextView tvOrderExplain;
    @BindView(R2.id.tv_cancel_order)
    public TextView tvCancelOrder;
    @BindView(R2.id.tv_pay_title)
    public TextView tvPayTitle;
    @BindView(R2.id.ll_wait_pay)
    public LinearLayout llWaitPay;
    @BindView(R2.id.tv_chat_online_second)
    public TextView tvChatOnlineSecond;
    @BindView(R2.id.ll_trade_warn)
    public LinearLayout llTradeWarn;
    @BindView(R2.id.tv_order_money_unit)
    public TextView tvOrderMoneyUnit;
    @BindView(R2.id.tv_num_unit)
    public TextView tvNumUnit;
    @BindView(R2.id.tv_single_price_unit)
    public TextView tvSinglePriceUnit;
    @BindView(R2.id.tv_receive_way)
    public TextView tvReceiveWay;
    @BindView(R2.id.tv_receive_account)
    public TextView tvReceiveAccount;
    @BindView(R2.id.tv_receive_name)
    public TextView tvReceiveName;
    @BindView(R2.id.tv_pay_amount)
    public TextView tvPayAmount;
    @BindView(R2.id.tv_pay_time)
    public TextView tvPayTime;
    @BindView(R2.id.ll_receive)
    public LinearLayout llReceive;
    @BindView(R2.id.ll_payment)
    public LinearLayout llPayment;

    private ClipboardManager mClipboardManager;

    public int mPayMentWay = 0;//0:银行卡   1：支付宝  2：微信

    public int mId;//订单id
    private ParisBuyOrderModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paris_buy_order;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mId = getIntent().getIntExtra(PageConstant.ID, 0);
        model = new ParisBuyOrderModel(this);
        model.uploadParisBuyOrderInfo();
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setColor(this, getResources().getColor(R.color.cl_2d3341));
        StatusBarUtils.StatusBarDarkMode(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mClipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
    }


    @OnClick({R2.id.tv_cancel_order, R2.id.tv_order_no, R2.id.tv_order_money, R2.id.tv_chat_online, R2.id.tv_confirm, R2.id.tv_chat_online_second})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_cancel_order) {
            //取消订单
           model.openCancelOrderDialog();
        } else if (id == R.id.tv_order_no) {
            //复制订单号
            if (!TextUtils.isEmpty(tvOrderNo.getText())) {
                mClipboardManager.setText(tvOrderNo.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvOrderNo.getText());
            }
        } else if (id == R.id.tv_order_money) {
            //复制订单金额
            if (!TextUtils.isEmpty(tvOrderMoney.getText())) {
                mClipboardManager.setText(tvOrderMoney.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvOrderMoney.getText());
            }
        } else if (id == R.id.tv_chat_online) {
            //在线闲聊
            new WaitingOpenDialog(this).builder().show();
        } else if (id == R.id.tv_confirm) {
            model.openConfirmPayDialog();
        } else if (id == R.id.tv_chat_online_second) {
            //在线闲聊
            new WaitingOpenDialog(this).builder().show();
        }
    }

    @OnClick({R2.id.tv_pay_way, R2.id.tv_bank_name, R2.id.tv_bank_receiver, R2.id.tv_bank_card_no, R2.id.tv_bank_belong,
            R2.id.tv_zfb_receiver, R2.id.tv_zfb_account, R2.id.iv_zfb_receive_pic,
            R2.id.tv_wechat_receiver, R2.id.tv_wechat_account, R2.id.iv_wechat_receive_pic})
    public void onViewClicked2(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_pay_way) {
            model.openSelectPaymentWayDialog();
        } else if (id == R.id.tv_bank_name) {
            //复制银行名
            if (!TextUtils.isEmpty(tvBankName.getText())) {
                mClipboardManager.setText(tvBankName.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvBankName.getText());
            }
        } else if (id == R.id.tv_bank_receiver) {
            //复制银行收款人
            if (!TextUtils.isEmpty(tvBankReceiver.getText())) {
                mClipboardManager.setText(tvBankReceiver.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvBankReceiver.getText());
            }
        } else if (id == R.id.tv_bank_card_no) {
            //复制银行卡号
            if (!TextUtils.isEmpty(tvBankCardNo.getText())) {
                mClipboardManager.setText(tvBankCardNo.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvBankCardNo.getText());
            }
        } else if (id == R.id.tv_bank_belong) {
            //复制隶属银行
            if (!TextUtils.isEmpty(tvBankBelong.getText())) {
                mClipboardManager.setText(tvBankBelong.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvBankBelong.getText());
            }
        } else if (id == R.id.tv_zfb_receiver) {
            //复制支付宝收款人
            if (!TextUtils.isEmpty(tvZfbReceiver.getText())) {
                mClipboardManager.setText(tvZfbReceiver.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvZfbReceiver.getText());
            }
        } else if (id == R.id.tv_zfb_account) {
            //复制支付宝账号
            if (!TextUtils.isEmpty(tvZfbAccount.getText())) {
                mClipboardManager.setText(tvZfbAccount.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvZfbAccount.getText());
            }
        } else if (id == R.id.iv_zfb_receive_pic) {
            //支付宝收款二维码
            ivZfbReceivePic.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(ivZfbReceivePic.getDrawingCache());
            ivZfbReceivePic.setDrawingCacheEnabled(false);
            if (bitmap != null) {
                new SaveReceivePicDialog(this)
                        .setBitmap(bitmap)
                        .builder()
                        .show();
            }
        } else if (id == R.id.tv_wechat_receiver) {
            //复制微信收款人
            if (!TextUtils.isEmpty(tvWechatReceiver.getText())) {
                mClipboardManager.setText(tvWechatReceiver.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvWechatReceiver.getText());
            }
        } else if (id == R.id.tv_wechat_account) {
            //复制微信账号
            if (!TextUtils.isEmpty(tvWechatAccount.getText())) {
                mClipboardManager.setText(tvWechatAccount.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvWechatAccount.getText());
            }
        } else if (id == R.id.iv_wechat_receive_pic) {
            //微信收款二维码
            ivWechatReceivePic.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(ivWechatReceivePic.getDrawingCache());
            ivWechatReceivePic.setDrawingCacheEnabled(false);
            if (bitmap != null) {
                new SaveReceivePicDialog(this)
                        .setBitmap(bitmap)
                        .builder()
                        .show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (model != null)
            model.cancelAll();
    }

}
