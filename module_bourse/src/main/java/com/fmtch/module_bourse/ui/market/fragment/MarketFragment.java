package com.fmtch.module_bourse.ui.market.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.MyPagerAdapter;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.listener.DataChangeListener;
import com.fmtch.module_bourse.listener.DataListener;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.market.model.MarketModel;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;
import com.fmtch.module_bourse.widget.FilterView;
import com.icechao.klinelib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by wtc on 2019/5/8
 */
public class MarketFragment extends LazyReFreshDataBaseFragment implements FilterView.onFilterListener {

    @BindView(R2.id.iv_search)
    ImageView ivSearch;
    @BindView(R2.id.tab_layout)
    LinearLayout tabLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    @BindView(R2.id.filter_coin)
    FilterView filterCoin;
    @BindView(R2.id.filter_last_price)
    FilterView filterLastPrice;
    @BindView(R2.id.filter_rise_fall)
    FilterView filterRiseFall;

    private List<Fragment> fragments = new ArrayList<>();
    private List<TextView> tabTitleList = new ArrayList<>();
    private List<View> tabUnderLineList = new ArrayList<>();
    private String[] titles = {"自选", "USDT", "ETH", "ET"};
    //不同的fragment
    public static final int MARKET_CHOOSE = 0;
    public static final int MARKET_USDT = 1;
    public static final int MARKET_BTC = 2;
    public static final int MARKET_ETH = 3;
    public static final int MARKET_ET = 4;
    //列表排序方式
    public static final int SORT_LETTER_UP = 100;
    public static final int SORT_LETTER_DOWN = 101;
    public static final int SORT_LAST_PRICE_UP = 200;
    public static final int SORT_LAST_PRICE_DOWN = 201;
    public static final int SORT_RISE_FALL_UP = 300;
    public static final int SORT_RISE_FALL_DOWN = 301;
    public static final int SORT_NORMAL = 400;

    private ArrayList<String> topics = new ArrayList<>();
    public boolean tickerSubscribeResult = false;   //行情数据是否订阅成功
    private MarketModel model;
    public static List<DataListener> dataListeners = new ArrayList<>();
    public static List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    public String tickerTopic;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_market;
    }

    @Override
    protected void initData() {
        super.initData();
        titles[0] = getString(R.string.optional);
        initBaseData();
        initBaseView();
    }

    @Override
    public void pollingData() {
        model.getData();
    }

    @Override
    public List<String> getTopics() {
        return topics;
    }

    @Override
    public SubscribeCallBack getSubscribeCallBack() {
        return model.getSubscribeCallBack(topics);
    }

    @Override
    public OnMessageCallBack getOnMessageCallBack() {
        return model.getOnMessageCallBack(topics);
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return model.getSocketConnectListener();
    }

    @Override
    public String getSubscribeKey() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        model.getData();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        model.dispatchData();
    }

    private void initBaseData() {
        //自选
        Bundle chooseBundle = new Bundle();
        chooseBundle.putInt(FRAGMENT_KEY, MARKET_CHOOSE);
        fragments.add(CoinFragment.newInstance(chooseBundle));
        //USDT
        Bundle USDTBundle = new Bundle();
        USDTBundle.putInt(FRAGMENT_KEY, MARKET_USDT);
        fragments.add(CoinFragment.newInstance(USDTBundle));
        //ETH
        Bundle ETHBundle = new Bundle();
        ETHBundle.putInt(FRAGMENT_KEY, MARKET_ETH);
        fragments.add(CoinFragment.newInstance(ETHBundle));
        //ETF
        Bundle ETFBundle = new Bundle();
        ETFBundle.putInt(FRAGMENT_KEY, MARKET_ET);
        fragments.add(CoinFragment.newInstance(ETFBundle));

        tickerTopic = TopicType.TICKER;
        topics.add(tickerTopic);
        model = new MarketModel(this);
    }

    protected void initBaseView() {
        super.initView();
        viewpager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), fragments));
        viewpager.setOffscreenPageLimit(fragments.size());
        initTab();

        filterCoin.setFilterListener(this);
        filterLastPrice.setFilterListener(this);
        filterRiseFall.setFilterListener(this);
    }

    private void initTab() {
        for (String title : titles) {
            View tab = LayoutInflater.from(getActivity()).inflate(R.layout.layout_tab_market, null);
            tabLayout.addView(tab);
            TextView tabTitle = tab.findViewById(R.id.tab_title);
            tabTitle.setText(title);
            tabTitleList.add(tabTitle);
            View tabUnderLine = tab.findViewById(R.id.tab_underline);
            tabUnderLineList.add(tabUnderLine);

            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int childCount = tabLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        if (v == tabLayout.getChildAt(i)) {
                            updateTabStyle(i);
                            viewpager.setCurrentItem(i, false);
                            break;
                        }
                    }
                }
            });
        }
        viewpager.setCurrentItem(1, false);
    }

    @OnPageChange(R2.id.viewpager)
    public void onPageSelected(int position) {
        updateTabStyle(position);
    }


    @Override
    public void onClickFilter(FilterView view, int type) {
        filterCoin.resetFilter();
        filterLastPrice.resetFilter();
        filterRiseFall.resetFilter();
        if (type == FilterView.FILTER_NORMAL) {
            setSortType(SORT_NORMAL);
            return;
        }
        int id = view.getId();
        int sortType = 0;
        if (id == R.id.filter_coin) {
            if (type == FilterView.FILTER_UP) {
                sortType = SORT_LETTER_UP;
            } else if (type == FilterView.FILTER_DOWN) {
                sortType = SORT_LETTER_DOWN;
            }
        } else if (id == R.id.filter_last_price) {
            if (type == FilterView.FILTER_UP) {
                sortType = SORT_LAST_PRICE_UP;
            } else if (type == FilterView.FILTER_DOWN) {
                sortType = SORT_LAST_PRICE_DOWN;
            }
        } else if (id == R.id.filter_rise_fall) {
            if (type == FilterView.FILTER_UP) {
                sortType = SORT_RISE_FALL_UP;
            } else if (type == FilterView.FILTER_DOWN) {
                sortType = SORT_RISE_FALL_DOWN;
            }
        }
        //设置排序方式
        setSortType(sortType);
    }

    private void setSortType(int sortType) {
        for (Fragment fragment : fragments) {
            CoinFragment coinFragment = (CoinFragment) fragment;
            coinFragment.setSortType(sortType);
        }
    }


    @OnClick(R2.id.iv_search)
    public void onViewClicked() {
        ARouter.getInstance().build(RouterMap.SEARCH_COIN).navigation();
    }

    /**
     * 更新tab样式
     *
     * @param position
     */
    private void updateTabStyle(int position) {
        for (int i = 0; i < titles.length; i++) {
            if (i == position) {
                tabTitleList.get(position).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                tabTitleList.get(position).setTextColor(getResources().getColor(R.color.theme));
                tabTitleList.get(position).getPaint().setFakeBoldText(true);
                tabUnderLineList.get(position).setVisibility(View.VISIBLE);
            } else {
                tabTitleList.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tabTitleList.get(i).setTextColor(getResources().getColor(R.color.cl_999999));
                tabTitleList.get(i).getPaint().setFakeBoldText(false);
                tabUnderLineList.get(i).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dataListeners.clear();
        dataChangeListeners.clear();
    }

    public void setDataListener(DataListener dataListener) {
        dataListeners.add(dataListener);
    }

    public void setDataChangeListener(DataChangeListener dataChangeListener) {
        dataChangeListeners.add(dataChangeListener);
    }
}
