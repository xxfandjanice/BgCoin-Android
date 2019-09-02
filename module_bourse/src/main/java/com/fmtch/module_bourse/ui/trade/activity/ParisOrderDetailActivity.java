package com.fmtch.module_bourse.ui.trade.activity;

import android.content.ClipboardManager;
import android.content.Context;
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
import com.fmtch.module_bourse.ui.trade.model.ParisOrderDetailModel;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.PARIS_COIN_ORDER_DETAIL, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ParisOrderDetailActivity extends BaseActivity {


    @BindView(R2.id.tv_title)
    public TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_status)
    public TextView tvStatus;
    @BindView(R2.id.tv_status_explain)
    public TextView tvStatusExplain;
    @BindView(R2.id.tv_order_no)
    public TextView tvOrderNo;
    @BindView(R2.id.tv_user_name)
    public TextView tvUserName;
    @BindView(R2.id.tv_order_money)
    public TextView tvOrderMoney;
    @BindView(R2.id.tv_order_num)
    public TextView tvOrderNum;
    @BindView(R2.id.tv_single_price)
    public TextView tvSinglePrice;
    @BindView(R2.id.tv_service_fee)
    public TextView tvServiceFee;
    @BindView(R2.id.tv_true_price)
    public TextView tvTruePrice;
    @BindView(R2.id.tv_order_time)
    public TextView tvOrderTime;
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
    @BindView(R2.id.tv_release_time)
    public TextView tvReleaseTime;
    @BindView(R2.id.ll_receive)
    public LinearLayout llReceive;
    @BindView(R2.id.ll_service_fee)
    public LinearLayout llServiceFee;
    @BindView(R2.id.ll_true_price)
    public LinearLayout llTruePrice;
    @BindView(R2.id.tv_order_money_unit)
    public TextView tvOrderMoneyUnit;
    @BindView(R2.id.tv_num_unit)
    public TextView tvNumUnit;
    @BindView(R2.id.tv_single_price_unit)
    public TextView tvSinglePriceUnit;
    @BindView(R2.id.tv_true_price_title)
    public TextView tvTruePriceTitle;
    @BindView(R2.id.iv_black)
    public ImageView ivBlack;
    @BindView(R2.id.tv_release_black)
    public TextView tvReleaseBlack;
    @BindView(R2.id.tv_chat_online)
    public TextView tvChatOnline;

    public int mId;//订单id
    private ParisOrderDetailModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paris_order_detail;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mId = getIntent().getIntExtra(PageConstant.ID, 0);
        model = new ParisOrderDetailModel(this);
        model.uploadParisOrderInfo();
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
    }

    @OnClick({R2.id.tv_order_no, R2.id.tv_chat_online, R2.id.iv_black, R2.id.tv_release_black})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.tv_order_no) {
            if (!TextUtils.isEmpty(tvOrderNo.getText())) {
                ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tvOrderNo.getText());
                ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + tvOrderNo.getText());
            }
        } else if (id == R.id.iv_black) {
            //拉黑
            model.addOrReleaseBlack(true);
        } else if (id == R.id.tv_release_black) {
            //解除拉黑
            model.addOrReleaseBlack(false);
        } else if (id == R.id.tv_chat_online) {
            //在线闲聊
            new WaitingOpenDialog(this).builder().show();
        }
    }

}
