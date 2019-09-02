package com.fmtch.module_bourse.ui.trade.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.module_bourse.Bourse_MainActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MyPagerAdapter;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wtc on 2019/5/8
 */
public class TradeFragment extends LazyBaseFragment {

    @BindView(R2.id.tv_paris_coin)
    TextView tvParisCoin;
    @BindView(R2.id.tv_coin_coin)
    TextView tvCoinCoin;
    @BindView(R2.id.view_pager)
    NoScrollViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private Coin_coinFragment coin_coinFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trade;
    }

    @Override
    protected void initData() {
        super.initData();
        coin_coinFragment = new Coin_coinFragment();
        fragments.add(new ParisCoinParentFragment());
        fragments.add(coin_coinFragment);
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), fragments));
        tvParisCoin.setSelected(false);
        tvCoinCoin.setSelected(true);
        viewPager.setCurrentItem(1);
    }

    @OnClick({R2.id.tv_paris_coin, R2.id.tv_coin_coin, R2.id.iv_drawer_menu, R2.id.iv_search})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_paris_coin) {
            viewPager.setCurrentItem(0, false);
            tvParisCoin.setSelected(true);
            tvCoinCoin.setSelected(false);
        } else if (id == R.id.tv_coin_coin) {
            viewPager.setCurrentItem(1, false);
            tvParisCoin.setSelected(false);
            tvCoinCoin.setSelected(true);
        } else if (id == R.id.iv_drawer_menu) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == 0) {
                ((Bourse_MainActivity) getActivity()).openDrawerLayout(Bourse_MainActivity.DRAWER_PARIS_COIN_TRADE_MENU);
            } else if (currentItem == 1) {
                ((Bourse_MainActivity) getActivity()).openDrawerLayout(Bourse_MainActivity.DRAWER_COIN_COIN_TRADE_MENU);
            }
        } else if (id == R.id.iv_search) {
            ARouter.getInstance().build(RouterMap.SEARCH_COIN).navigation();
        }

    }

    public String getCoinCoinSymbol() {
        return coin_coinFragment.getSymbol();
    }

}
