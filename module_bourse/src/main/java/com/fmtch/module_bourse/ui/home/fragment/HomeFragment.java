package com.fmtch.module_bourse.ui.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.Bourse_MainActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MyTabPagerAdapter;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.bean.BannerBean;
import com.fmtch.module_bourse.bean.Data;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.bean.NoticeBean;
import com.fmtch.module_bourse.ui.home.model.HomeModel;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.fmtch.module_bourse.widget.NoticeView;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by wtc on 2019/5/8
 */
public class HomeFragment extends LazyBaseFragment implements NoticeView.OnNoticeClickListener {
    @BindView(R2.id.iv_mine)
     ImageView ivMine;
    @BindView(R2.id.banner)
    public Banner banner;
    @BindView(R2.id.notice_view)
    public NoticeView noticeView;
    @BindView(R2.id.data_rv)
    public RecyclerView dataRv;
    @BindView(R2.id.iv_invite_friend)
    public ImageView ivInviteFriend;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    @BindViews({R2.id.tab_rise_fall, R2.id.tab_transaction, R2.id.tab_new_coin})
    List<TextView> tabList;

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    public List<BannerBean> bannerUrls = new ArrayList<>();
    public List<MarketBean> dataBanners = new ArrayList<>();
    public List<NoticeBean> notices = new ArrayList<>();
    private HomeModel homeModel;

    public static final int HOME_RISE_FALL_LIST = 0;   //涨幅榜
    public static final int HOME_TRANSACTION_LIST = 1; //成交榜
    public static final int HOME_NEW_COIN_LIST = 2;    //新币榜

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        super.initData();
        titles.add(getString(R.string.rise_fall_list));
        titles.add(getString(R.string.transaction_list));
        titles.add(getString(R.string.new_coin_list));
        //涨幅榜
        Bundle riseFallBundle = new Bundle();
        riseFallBundle.putInt(FRAGMENT_KEY,HOME_RISE_FALL_LIST);
        fragments.add(RiseOrFallListFragment.newInstance(riseFallBundle));
        //成交额榜
        Bundle transactionBundle = new Bundle();
        transactionBundle.putInt(FRAGMENT_KEY, HOME_TRANSACTION_LIST);
        fragments.add(RiseOrFallListFragment.newInstance(transactionBundle));
        //新币榜
        Bundle newCoinBundle = new Bundle();
        newCoinBundle.putInt(FRAGMENT_KEY, HOME_NEW_COIN_LIST);
        fragments.add(RiseOrFallListFragment.newInstance(newCoinBundle));
    }

    @Override
    protected void initView() {
        super.initView();
        noticeView.setOnNoticeClickListener(this);
        viewpager.setAdapter(new MyTabPagerAdapter(getChildFragmentManager(), fragments, titles));
        viewpager.setOffscreenPageLimit(3);
        tabList.get(0).setSelected(true);

        homeModel = new HomeModel(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //Banner点击监听
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (bannerUrls.size() > 0) {
                    BannerBean bannerBean = bannerUrls.get(position);
                    ARouter.getInstance().build(RouterMap.WEB_VIEW)
                            .withString(PageConstant.TARGET_URL, bannerBean.getLink())
                            .navigation();
                }
            }
        });
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        //加载数据
        homeModel.loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.CHANGE_UNIT) {
            homeModel.updateDataBanner();
        }
    }

    /**
     * 系统公告-点击事件
     *
     * @param position
     * @param notice
     */
    @Override
    public void onNoticeClick(int position, NoticeBean notice) {
        ARouter.getInstance().build(RouterMap.WEB_VIEW)
                .withString(PageConstant.TARGET_URL, notice.getHtml_url())
                .navigation();
    }

    @OnClick({R2.id.iv_mine, R2.id.iv_invite_friend, R2.id.tab_rise_fall, R2.id.tab_transaction, R2.id.tab_new_coin})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_mine) {
            ((Bourse_MainActivity) getActivity()).openDrawerLayout(Bourse_MainActivity.DRAWER_MINE);
        } else if (id == R.id.iv_invite_friend) {
            ARouter.getInstance().build(RouterMap.INVITE_FRIENDS).navigation();
        } else if (id == R.id.tab_rise_fall) {
            viewpager.setCurrentItem(0, false);
        } else if (id == R.id.tab_transaction) {
            viewpager.setCurrentItem(1, false);
        } else if (id == R.id.tab_new_coin) {
            viewpager.setCurrentItem(2, false);
        }
    }

    @OnPageChange(R2.id.viewpager)
    public void onPageSelected(int position) {
        viewpager.setCurrentItem(position);
        tabList.get(0).setSelected(false);
        tabList.get(1).setSelected(false);
        tabList.get(2).setSelected(false);
        tabList.get(position).setSelected(true);
    }
}
