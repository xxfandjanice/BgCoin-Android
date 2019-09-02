package com.fmtch.mine.mvp.view.activity;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.DataCleanManager;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.LocaleManager;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;
import com.fmtch.base.utils.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.SETTING)
public class SettingActivity extends BaseActivity {
    //标题栏
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_cache)
    TextView tvCache;
    @BindView(R2.id.tv_language)
    TextView tvLanguage;
    @BindView(R2.id.tv_unit)
    TextView tvUnit;
    @BindView(R2.id.tv_refresh_times)
    TextView tvRefreshTimes;

    private int mMarketRefreshTimes;//1：行情刷新频率 2：始终实时行情 3：仅在WI-FI下实时行情

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_setting;
    }

    @Override
    protected void initView() {
        super.initView();
        //初始化标题栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (TextUtils.equals(LocaleManager.getLanguage(this), LocaleManager.LANGUAGE_CHINESE)) {
            //中文
            tvLanguage.setText(R.string.mine_chinese);
        } else {
            //英文
            tvLanguage.setText(R.string.mine_english);
        }
        mMarketRefreshTimes = (int) SpUtils.get(KeyConstant.KEY_MARKET_REFRESH_TIMES, 3);//默认仅在WI-FI下实时行情
        switch (mMarketRefreshTimes) {
            case 1:
                tvRefreshTimes.setText(R.string.mine_market_refresh_1);
                break;
            case 2:
                tvRefreshTimes.setText(R.string.mine_market_refresh_2);
                break;
            case 3:
                tvRefreshTimes.setText(R.string.mine_market_refresh_3);
                break;
        }
        try {
            tvCache.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currencyUnit = (String) SpUtils.get(KeyConstant.KEY_PREF_CURRENCY_UNIT, "CNY");
        if (TextUtils.equals(currencyUnit, "CNY")) {
            tvUnit.setText(R.string.mine_unit_rmb);
        } else {
            tvUnit.setText(R.string.mine_unit_usd);
        }
    }


    @OnClick({R2.id.ll_language, R2.id.ll_price_unit, R2.id.ll_market_refresh, R2.id.ll_clear_cache, R2.id.tv_exit_login})
    public void onViewClicked(View view) {
        if (ToastUtils.isFastClick()) {
            return;
        }
        int id = view.getId();
        if (id == R.id.ll_language) {
            //语言
            ARouter.getInstance().build(RouterMap.SETTING_LANGUAGE).navigation();
        } else if (id == R.id.ll_price_unit) {
            //计价方式
            ARouter.getInstance().build(RouterMap.SETTING_CURRENCY_UNIT).navigation();
        } else if (id == R.id.ll_market_refresh) {
            //行情刷新频率
            openSelectTimesDialog();
        } else if (id == R.id.ll_clear_cache) {
            //点击清除缓存
            DataCleanManager.clearAllCache(this);
            ToastUtils.showLongToast(R.string.mine_clear_cache_success);
            tvCache.setText("0KB");
        } else if (id == R.id.tv_exit_login) {
            //点击退出登录
            CommonDialog exitDialog = new CommonDialog(this);
            exitDialog.showMsg(getString(R.string.mine_exit_login_explain));
            exitDialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    RequestUtil.requestGet(API.USER_EXIT_LOGIN,new OnResponseListenerImpl(),SettingActivity.this);
                    EventBus.getDefault().post(new EventBean<>(EventType.USER_EXIT));
                    finish();
                }
            });
            exitDialog.show();
        }
    }

    public void openSelectTimesDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_market_refresh_time, null);
        final TextView tvfirst = view.findViewById(R.id.tv_first);
        TextView tvSecond = view.findViewById(R.id.tv_second);
        TextView tvThird = view.findViewById(R.id.tv_third);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        switch (mMarketRefreshTimes) {
            case 1:
                tvfirst.setTextColor(getResources().getColor(R.color.theme));
                tvSecond.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvThird.setTextColor(getResources().getColor(R.color.color_common_text99));
                break;
            case 2:
                tvfirst.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvSecond.setTextColor(getResources().getColor(R.color.theme));
                tvThird.setTextColor(getResources().getColor(R.color.color_common_text99));
                break;
            case 3:
                tvfirst.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvSecond.setTextColor(getResources().getColor(R.color.color_common_text99));
                tvThird.setTextColor(getResources().getColor(R.color.theme));
                break;
        }
        final BottomSheetDialog mDialog = DialogUtils.showDetailBottomDialog(this, view);
        tvfirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mMarketRefreshTimes = 1;
                SpUtils.put(KeyConstant.KEY_MARKET_REFRESH_TIMES,1);
                tvRefreshTimes.setText(R.string.mine_market_refresh_1);
            }
        });
        tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mMarketRefreshTimes = 2;
                SpUtils.put(KeyConstant.KEY_MARKET_REFRESH_TIMES,2);
                tvRefreshTimes.setText(R.string.mine_market_refresh_2);
            }
        });
        tvThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mMarketRefreshTimes = 3;
                SpUtils.put(KeyConstant.KEY_MARKET_REFRESH_TIMES,3);
                tvRefreshTimes.setText(R.string.mine_market_refresh_3);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

}
