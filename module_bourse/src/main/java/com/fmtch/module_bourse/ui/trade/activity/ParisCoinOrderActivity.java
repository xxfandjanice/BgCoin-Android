package com.fmtch.module_bourse.ui.trade.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.flyco.tablayout.SlidingTabLayout;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.dialog.WaitingOpenDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.ui.trade.fragment.ParisCoinOrderStateFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.PARIS_COIN_ORDER, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ParisCoinOrderActivity extends BaseActivity {

    @BindView(R2.id.iv_message)
    ImageView ivMessage;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tab_layout)
    SlidingTabLayout tabLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;

    private String[] tabs;
    private ArrayList<Fragment> fragments;

    public static int UNFINISHED = 0x100;  //未完成
    public static int FINISHED = 0x101;    //已完成
    public static int CANCEL = 0x102;      //已取消

    @Override
    protected int getLayoutId() {
        return R.layout.activity_paris_coin_order;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        fragments = new ArrayList<>();
        tabs = new String[]{getString(R.string.unfinished), getString(R.string.finished), getString(R.string.canceled)};
        for (int i = 0; i < tabs.length; i++) {
            Bundle bundle = new Bundle();
            if (i == 0) {
                bundle.putInt(PageConstant.KEY, UNFINISHED);
            } else if (i == 1) {
                bundle.putInt(PageConstant.KEY, FINISHED);
            } else if (i == 2) {
                bundle.putInt(PageConstant.KEY, CANCEL);
            }
            fragments.add(ParisCoinOrderStateFragment.newInstance(bundle));
        }
        tabLayout.setViewPager(viewpager, tabs, this, fragments);
        viewpager.setOffscreenPageLimit(fragments.size());
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

    @OnClick(R2.id.iv_message)
    public void onViewClicked() {
        new WaitingOpenDialog(this).builder().show();
    }
}
