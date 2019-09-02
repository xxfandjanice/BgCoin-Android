package com.fmtch.module_bourse.ui.home.drawer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.pojo.MyChooseBean;
import com.fmtch.base.ui.BaseFragment;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.widget.ClearEditText;
import com.fmtch.module_bourse.Bourse_MainActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.utils.LogUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by wtc on 2019/7/30
 */
public class CoinCoinTradeDrawerFragment extends BaseFragment {


    @BindView(R2.id.drawer_trade_filter_tab)
    TabLayout drawerTradeFilterTabLayout;
    @BindView(R2.id.drawer_trade_filter_search)
    ClearEditText drawerTradeFilterSearch;
    @BindView(R2.id.drawer_trade_filter_rv)
    RecyclerView drawerTradeFilterRv;
    private DrawerLayout drawerLayout;

    public RealmResults<MyChooseBean> myChooseList; //交易抽屉-我的自选
    public List<MarketBean> drawCoinFilterList;  //交易抽屉-币种数据
    public List<MarketBean> drawCoinFilterListAll;  //交易抽屉-币种数据
    public BaseQuickPageStateAdapter<MarketBean, BaseViewHolder> drawCoinAdapter;
    final String[] titles = {"自选", "USDT", "ETH", "ET"};

    @Override
    protected int getLayoutId() {
        return R.layout.layout_drawer_trade_filter;
    }

    @Override
    protected void initData() {
        super.initData();
        titles[0] = getString(R.string.optional);
        updateMyChoose();
        initDrawerTradeFilter();
        //加载币种数据
        getCoinList();
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getCoinList();
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //币种tab切换监听
        drawerTradeFilterTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switchTab(titles[position]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //币种列表点击监听
        drawCoinAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MarketBean bean = drawCoinFilterList.get(position);
                EventBean<MarketBean> eventBean = new EventBean<>(EventType.SELECT_SYMBOL);
                eventBean.setData(bean);
                EventBus.getDefault().post(eventBean);
                drawerLayout.closeDrawers();
            }
        });
        //币种搜索监听
        drawerTradeFilterSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCoin(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 当打开币种选择抽屉时获取本地自选交易对
     */
    void updateMyChoose() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                myChooseList = realm.where(MyChooseBean.class).findAll();
            }
        });
    }

    /**
     * 抽屉-交易页面-币种筛选
     */
    private void initDrawerTradeFilter() {
        for (String title : titles) {
            drawerTradeFilterTabLayout.addTab(drawerTradeFilterTabLayout.newTab().setText(title));
        }
        drawCoinFilterList = new ArrayList<>();
        drawCoinFilterListAll = new ArrayList<>();
        drawerTradeFilterRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        drawCoinAdapter = new BaseQuickPageStateAdapter<MarketBean, BaseViewHolder>(getActivity(), R.layout.item_drawer_trade_filter, drawCoinFilterList) {
            @Override
            protected void convert(BaseViewHolder helper, MarketBean item) {
                helper.setText(R.id.tv_name1, item.getCoin_name());
                helper.setText(R.id.tv_name2, item.getMarket_name());
                helper.setText(R.id.tv_last_prices, NumberUtils.stripMoneyZeros(item.getClose()));
                BigDecimal rate = NumberUtils.getRate(item.getClose(), item.getOpen());
                if (rate.compareTo(BigDecimal.ZERO) > 0) {
                    helper.setTextColor(R.id.tv_last_prices, getResources().getColor(R.color.cl_03c087));
                } else {
                    helper.setTextColor(R.id.tv_last_prices, getResources().getColor(R.color.cl_f55758));
                }
            }
        };
        drawerTradeFilterRv.setAdapter(drawCoinAdapter);
        drawerTradeFilterTabLayout.getTabAt(1).select();
    }

    /**
     * 交易页面-抽屉-币种选择
     */
    public void getCoinList() {
        drawCoinAdapter.showLoadingPage();
        RetrofitManager.getInstance().create(ApiService.class)
                .getMarkets("")
                .subscribeOn(Schedulers.io())
                .compose(this.<BaseResponse<List<MarketBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MarketBean>>() {
                    @Override
                    public void onSuccess(List<MarketBean> data) {
                        if (data != null) {
                            drawCoinFilterListAll.clear();
                            drawCoinFilterListAll.addAll(data);
                            for (MarketBean bean : drawCoinFilterListAll) {
                                String[] split = bean.getSymbol().split("/");
                                bean.setCoin_name(split[0]);
                                bean.setMarket_name(split[1]);
                            }
                            int position = drawerTradeFilterTabLayout.getSelectedTabPosition();
                            switchTab(titles[position]);
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        drawCoinAdapter.showErrorPage(TextUtils.isEmpty(response.getMessage()) ? "" : response.getMessage());
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        drawCoinAdapter.showErrorPage("");
                    }
                });
    }


    /**
     * 切换币种
     *
     * @param title 币种名
     */
    public void switchTab(String title) {
        if (drawCoinFilterList == null || drawCoinAdapter == null || TextUtils.isEmpty(title)) {
            return;
        }
        drawCoinFilterList.clear();
        if (title.equals("自选")) {
            if (myChooseList != null && myChooseList.size() > 0) {
                for (MyChooseBean myChooseBean : myChooseList) {
                    for (MarketBean bean : drawCoinFilterListAll) {
                        if (myChooseBean.getSymbol().equals(bean.getSymbol())) {
                            drawCoinFilterList.add(bean);
                        }
                    }
                }
            }
        } else {
            for (MarketBean bean : drawCoinFilterListAll) {
                if (bean.getMarket_name().equals(title)) {
                    drawCoinFilterList.add(bean);
                }
            }
        }
        if (drawCoinFilterList.size() == 0) {
            drawCoinAdapter.showEmptyPage();
        } else {
            drawCoinAdapter.setNewData(drawCoinFilterList);
        }
    }

    /**
     * 搜索币种
     *
     * @param input 搜索内容
     */
    public void searchCoin(String input) {
        if (drawCoinFilterList == null || drawCoinAdapter == null) {
            return;
        }
        try {
            String temp = "";
            if (TextUtils.isEmpty(input)) {
                int position = drawerTradeFilterTabLayout.getSelectedTabPosition();
                temp = titles[position];
            }
            drawCoinFilterList.clear();
            Pattern pattern;
            Matcher matcherName;
            if (TextUtils.isEmpty(input)) {
                pattern = Pattern.compile(temp, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(input, Pattern.CASE_INSENSITIVE);
            }
            for (MarketBean bean : drawCoinFilterListAll) {
                if (TextUtils.isEmpty(input)) {
                    matcherName = pattern.matcher(bean.getMarket_name());
                } else {
                    matcherName = pattern.matcher(bean.getCoin_name());
                }
                if (matcherName.find()) {
                    drawCoinFilterList.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (drawCoinFilterList.size() == 0) {
            drawCoinAdapter.showEmptyPage();
        } else {
            drawCoinAdapter.setNewData(drawCoinFilterList);
        }
    }
}
