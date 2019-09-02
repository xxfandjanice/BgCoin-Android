package com.fmtch.module_bourse.ui.property.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.FileUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.ui.property.model.RechargeCoinModel;


import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.RECHARGE_COIN, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class RechargeCoinActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_coin_name)
    public TextView tvCoinName;
    @BindView(R2.id.tv_recharge_address)
    public TextView tvRechargeAddress;
    @BindView(R2.id.iv_qr_code)
    public ImageView ivQrCode;
    @BindView(R2.id.tv_warning_content)
    public TextView tvWarningContent;
    @BindView(R2.id.tv_tag)
    public TextView tvTag;
    @BindView(R2.id.tv_copy_tag)
    public TextView tvCopyTag;

    public AccountBean coinInfo;

    private RechargeCoinModel model;

    public String mRechargeAddress;//充值地址
    public Bitmap mQrCodeBitmap;//地址二维码
    public String mTag;//标签

    private final static int REQUEST_CODE = 8;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge_coin;
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
        model = new RechargeCoinModel(this);
        if (getIntent().getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) getIntent().getSerializableExtra(PageConstant.COIN_INFO);
            model.getCoinInfo(coinInfo.getCoin_id());
        }
    }

    //充币记录
    @OnClick(R2.id.iv_records)
    public void onViewClicked() {
        ARouter.getInstance().build(RouterMap.TRANSFER_COIN_RECORDS)
                .withInt(PageConstant.TYPE, 1)
                .withSerializable(PageConstant.COIN_INFO, coinInfo)
                .navigation();
    }

    //选择币种
    @OnClick(R2.id.tv_select_coin)
    public void onTvSelectCoinClicked() {
        ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                .withInt(PageConstant.TYPE, 1)
                .navigation(this, REQUEST_CODE);
    }

    //复制地址
    @OnClick(R2.id.tv_copy_address)
    public void onTvCopyAddressClicked() {
        if (!TextUtils.isEmpty(mRechargeAddress)) {
            ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(mRechargeAddress);
            ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + mRechargeAddress);
        }
    }

    //复制标签
    @OnClick(R2.id.tv_copy_tag)
    public void onCopyTagClicked() {
        if (!TextUtils.isEmpty(mTag)) {
            ClipboardManager cmb = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(mTag);
            ToastUtils.showLongToast(getString(com.fmtch.base.R.string.copy_success_) + mTag);
        }
    }

    //保存二维码
    @OnClick(R2.id.tv_download_qr_code)
    public void onTvDownloadQrCodeClicked() {
        FileUtils.savePic(this, mQrCodeBitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null && data.getSerializableExtra(PageConstant.COIN_INFO) != null) {
            AccountBean coinInfo = (AccountBean) data.getSerializableExtra(PageConstant.COIN_INFO);
            model.getCoinInfo(coinInfo.getCoin_id());
        }
    }

}
