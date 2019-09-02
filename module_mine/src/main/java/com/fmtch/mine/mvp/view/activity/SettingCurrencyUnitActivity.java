package com.fmtch.mine.mvp.view.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = RouterMap.SETTING_CURRENCY_UNIT)
public class SettingCurrencyUnitActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.iv_rmb)
    ImageView ivRmb;
    @BindView(R2.id.iv_usd)
    ImageView ivUsd;
    //语言
    private String currencyUnit;

    private boolean isCnyChecked;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_setting_currency_unit;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        currencyUnit = (String) SpUtils.get(KeyConstant.KEY_PREF_CURRENCY_UNIT, "CNY");
        isCnyChecked = TextUtils.equals(currencyUnit, "CNY");
        refreshChecked();
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

    private void refreshChecked() {
        if (isCnyChecked) {
            ivRmb.setImageResource(R.drawable.icon_checked);
            ivUsd.setImageResource(R.drawable.icon_unchecked);
        } else {
            ivUsd.setImageResource(R.drawable.icon_checked);
            ivRmb.setImageResource(R.drawable.icon_unchecked);
        }
    }

    @OnClick(R2.id.tv_save)
    public void onViewClicked() {
        if (ToastUtils.isFastClick()) {
            return;
        }
        ToastUtils.showLongToast(R.string.set_success);
        SpUtils.put(KeyConstant.KEY_PREF_CURRENCY_UNIT, isCnyChecked ? "CNY" : "USD");
        EventBus.getDefault().post(new EventBean<>(EventType.CHANGE_UNIT));
        finish();
    }

    @OnClick({R2.id.ll_rmb, R2.id.ll_usd})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.ll_rmb) {
            if (!isCnyChecked) {
                isCnyChecked = true;
                refreshChecked();
            }
        } else if (id == R.id.ll_usd) {
            if (isCnyChecked) {
                isCnyChecked = false;
                refreshChecked();
            }
        }

    }
}
