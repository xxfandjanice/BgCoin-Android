package com.fmtch.module_bourse.ui.property.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.RateInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MyTabPagerAdapter;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.ui.property.model.PropertyModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by wtc on 2019/5/8
 */
public class PropertyFragment extends LazyBaseFragment {
    @BindView(R2.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.app_bar)
    AppBarLayout appbar;
    @BindView(R2.id.ll_top_view)
    LinearLayout llTopView;
    @BindView(R2.id.iv_hide_show_money)
    ImageView ivHideShowMoney;
    @BindView(R2.id.tv_total_money)
    public TextView tvTotalMoney;
    @BindView(R2.id.tv_total_coin)
    public TextView tvTotalCoin;

    public static final int PROPERTY_MY_ACCOUNT = 0;
    public static final int PROPERTY_COIN_COIN_ACCOUNT = 1;
    public static final int PROPERTY_PARIS_COIN_ACCOUNT = 2;

    private boolean isHideMoney = false;    //是否明文显示总资产
    private String hideText = "*****";
    private AccountFragment myAccountFragment;
    private AccountFragment coinCoinFragment;
    private AccountFragment parisCoinFragment;

    private PropertyModel model;
    public String totalBTC;
    public RealmList<CoinToBTC> coinToBTCList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_property;
    }

    @Override
    protected void initData() {
        super.initData();
        totalBTC = "";
        model = new PropertyModel(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //appBarLayout滑动监听
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                toolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.theme), Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange()));
            }
        });
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        List<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.my_wallet));
        titles.add(getResources().getString(R.string.coin_coin_account));
        titles.add(getResources().getString(R.string.paris_coin_account));
        List<Fragment> fragments = new ArrayList<>();
        //我的钱包
        Bundle myAccountBundle = new Bundle();
        myAccountBundle.putInt(FRAGMENT_KEY, PROPERTY_MY_ACCOUNT);
        myAccountFragment = AccountFragment.newInstance(myAccountBundle);
        fragments.add(myAccountFragment);
        //币币账户
        Bundle coinCoinAccountBundle = new Bundle();
        coinCoinAccountBundle.putInt(FRAGMENT_KEY, PROPERTY_COIN_COIN_ACCOUNT);
        coinCoinFragment = AccountFragment.newInstance(coinCoinAccountBundle);
        fragments.add(coinCoinFragment);
        //法币账户
        Bundle parisCoinAccountBundle = new Bundle();
        parisCoinAccountBundle.putInt(FRAGMENT_KEY, PROPERTY_PARIS_COIN_ACCOUNT);
        parisCoinFragment = AccountFragment.newInstance(parisCoinAccountBundle);
        fragments.add(parisCoinFragment);

        viewpager.setAdapter(new MyTabPagerAdapter(getChildFragmentManager(), fragments, titles));
        viewpager.setOffscreenPageLimit(titles.size());
        tabLayout.setupWithViewPager(viewpager);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (model != null && !TextUtils.isEmpty((String) SpUtils.get(KeyConstant.KEY_LOGIN_TOKEN, ""))) {
            RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
            if (rateInfo != null) {
                coinToBTCList = rateInfo.getCoin_to_btc_list();
            }
            model.getData();
        }
    }


    @OnClick({R2.id.iv_hide_show_money, R2.id.tv_recharge, R2.id.tv_transform, R2.id.tv_draw_coin})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_hide_show_money) {
            //隐藏、显示总资产
            isHideMoney = !isHideMoney;

            ivHideShowMoney.setImageResource(isHideMoney ? R.mipmap.icon_hide_property : R.mipmap.icon_show_property);
            tvTotalCoin.setText(isHideMoney ? hideText : totalBTC);
            tvTotalMoney.setText(isHideMoney ? hideText : "≈ " + NumberUtils.getBTCToMoneyWithUnit(totalBTC));
            myAccountFragment.setMoneyVisible(isHideMoney);
            coinCoinFragment.setMoneyVisible(isHideMoney);
            parisCoinFragment.setMoneyVisible(isHideMoney);

        } else if (id == R.id.tv_recharge) {
            //充币
            ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                    .withInt(PageConstant.TYPE, 1)
                    .withBoolean(PageConstant.JUMP, true)
                    .navigation();
        } else if (id == R.id.tv_transform) {
            //资金划转
            ARouter.getInstance().build(RouterMap.TRANSFER_COIN).navigation();
        } else if (id == R.id.tv_draw_coin) {
            //提币
            UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
            if (userLoginInfo == null) {
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
            } else if (userLoginInfo.getKyc_status() == 0) {
                //未实名认证
                CommonDialog dialog = new CommonDialog(getActivity());
                dialog.showMsg(getString(R.string.no_auth), false);
                dialog.setBtnConfirmText(getString(R.string.go_auth));
                dialog.show();
                dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                    @Override
                    public void onConfirmClick() {
                        ARouter.getInstance().build(RouterMap.AUTH).navigation();
                    }
                });
            } else if (userLoginInfo.getKyc_status() == 1) {
                //已经实名认证
                ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                        .withInt(PageConstant.TYPE, 2)
                        .withBoolean(PageConstant.JUMP, true)
                        .navigation();
            } else {
                //申请实名认证进入实名认证状态
                ARouter.getInstance().build(RouterMap.AUTH_STATUS).navigation();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.CHANGE_UNIT) {
            tvTotalMoney.setText(isHideMoney ? hideText : "≈ " + NumberUtils.getBTCToMoneyWithUnit(totalBTC));
        }
    }

    /**
     * 根据百分比改变颜色透明度
     */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }
}
