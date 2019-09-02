package com.fmtch.module_bourse.ui.trade.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.PaymentBean;
import com.fmtch.module_bourse.ui.trade.model.PaymentSettingModel;
import com.fmtch.module_bourse.widget.dialog.PaymentWaySelectDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.PAYMENT_SETTING, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class PaymentSettingActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.rv)
    RecyclerView rv;


    private int mPayMentWay = 0;//0:银行卡   1：支付宝  2：微信

    public static final int REQUEST_CODE = 666;

    private PaymentSettingModel model;
    public List<PaymentBean> list;

    public BaseQuickPageStateAdapter mAdapter;
    private View mEmptyView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_payment_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();
        model = new PaymentSettingModel(this);
        mAdapter = new BaseQuickPageStateAdapter<PaymentBean, BaseViewHolder>(this, R.layout.item_payment_setting, list) {
            @Override
            protected void convert(BaseViewHolder helper, PaymentBean item) {
                int type = item.getType();//0:银行卡   1：支付宝  2：微信
                int position = helper.getAdapterPosition();
                if (position == 0) {
                    helper.setVisible(R.id.tv_type, true);
                } else {
                    PaymentBean lastItem = list.get(position - 1);
                    if (item.getType() == lastItem.getType()) {
                        helper.setGone(R.id.tv_type, false);
                    } else {
                        helper.setGone(R.id.tv_type, true);
                    }
                }
                TextView tvType = helper.getView(R.id.tv_type);
                switch (type) {
                    case 0:
                        tvType.setText(R.string.bank_transfer);
                        tvType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_bank_card, 0, 0, 0);
                        helper.setBackgroundRes(R.id.content, R.drawable.shape_sfk_bank)
                                .setText(R.id.tv_title, item.getBank_name())
                                .setText(R.id.tv_content, item.getAccount());
                        break;
                    case 1:
                        tvType.setText(R.string.zfb);
                        tvType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_zfb, 0, 0, 0);
                        helper.setBackgroundRes(R.id.content, R.drawable.shape_sfk_zfb)
                                .setText(R.id.tv_title, item.getName())
                                .setText(R.id.tv_content, item.getAccount());
                        break;
                    case 2:
                        tvType.setText(R.string.wechat);
                        tvType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay_wechat, 0, 0, 0);
                        helper.setBackgroundRes(R.id.content, R.drawable.shape_sfk_wechat)
                                .setText(R.id.tv_title, item.getName())
                                .setText(R.id.tv_content, item.getAccount());
                        break;
                }
                helper.addOnClickListener(R.id.tv_status)
                        .addOnClickListener(R.id.tv_unbind);
                TextView tvStatus = helper.getView(R.id.tv_status);
                if (item.getIs_open() == 1) {
                    helper.setText(R.id.tv_status, R.string.opened);
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_sfk_button_on, 0, 0);
                } else {
                    helper.setBackgroundRes(R.id.content, R.drawable.shape_sfk_off)
                            .setText(R.id.tv_status, R.string.closed);
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_sfk_button_off, 0, 0);
                }

            }
        };
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (ToastUtils.isFastClick())
                    return;
                PaymentBean paymentBean = (PaymentBean) adapter.getItem(position);
                if (paymentBean == null)
                    return;
                int id = view.getId();
                if (id == R.id.tv_status) {
                    model.openOrClosePayment(paymentBean.getId());
                } else if (id == R.id.tv_unbind) {
                    model.unbindPayment(paymentBean.getId());
                }
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);

        model.uploadPaymentList();
    }

    public void showEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(this).inflate(R.layout.layout_empty_payment, null);
            mEmptyView.findViewById(R.id.tv_add).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ToastUtils.isFastClick())
                        return;
                    openPaymentWaySelectDialog();
                }
            });
        }
        mAdapter.setEmptyView(mEmptyView);
    }

    @OnClick({R2.id.iv_add})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick())
            return;
        int id = view.getId();
        if (id == R.id.iv_add) {
            openPaymentWaySelectDialog();
        }
    }

    //选择方式
    private void openPaymentWaySelectDialog() {
        new PaymentWaySelectDialog(this)
                .setSelectPos(mPayMentWay)
                .setSelectPayWayListener(new PaymentWaySelectDialog.SelectPayWayListener() {

                    @Override
                    public void selectPayment(int type, PaymentBean paymentBean) {
                        mPayMentWay = type;
                        //0:银行卡   1：支付宝  2：微信
                        ARouter.getInstance().build(RouterMap.ADD_PAYMENT)
                                .withInt(PageConstant.TYPE, mPayMentWay)
                                .navigation(PaymentSettingActivity.this, REQUEST_CODE);
                    }
                })
                .builder()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == REQUEST_CODE) {
            model.uploadPaymentList();
        }
    }

}
