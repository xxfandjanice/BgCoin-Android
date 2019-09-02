package com.fmtch.module_bourse.ui.trade.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.trade.fragment.AttentionFragment;
import com.fmtch.module_bourse.ui.trade.fragment.BlackListFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.BLACKLIST_ATTENTION, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class BlacklistAttentionActivity extends BaseActivity {

    @BindView(R2.id.tab_layout)
    SlidingTabLayout tabLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;


    private String[] tabs;
    private ArrayList<Fragment> fragments;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blacklist_attention;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        fragments = new ArrayList<>();
        fragments.add(new BlackListFragment());
        fragments.add(new AttentionFragment());
        tabs = new String[]{getString(R.string.black_list), getString(R.string.attention)};
        tabLayout.setViewPager(viewpager, tabs, this, fragments);
        viewpager.setOffscreenPageLimit(fragments.size());
    }

    @OnClick(R2.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
