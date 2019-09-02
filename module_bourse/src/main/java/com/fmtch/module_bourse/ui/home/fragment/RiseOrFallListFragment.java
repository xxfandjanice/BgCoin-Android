package com.fmtch.module_bourse.ui.home.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.bean.RiseFallBean;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.model.RiseFallModel;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by wtc on 2019/5/9
 */
public class RiseOrFallListFragment extends LazyReFreshDataBaseFragment {

    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.tv_rise_fall)
    TextView tvRiseFall;
    @BindView(R2.id.tv_close)
    TextView tvClose;
    public BaseQuickPageStateAdapter<RiseFallBean, BaseViewHolder> adapter;
    public List<RiseFallBean> list;
    private int fragmentType = -1;   //区分 涨幅榜 0  成交额榜 1  新币榜 2
    private RiseFallModel model;

    public static RiseOrFallListFragment newInstance(Bundle bundle) {
        RiseOrFallListFragment fragment = new RiseOrFallListFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rise_fall_list;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        model = new RiseFallModel(this);
        fragmentType = bundle.getInt(FRAGMENT_KEY);
        updateTitle();
        initAdapter();
    }

    private void updateTitle() {
        if (fragmentType == HomeFragment.HOME_TRANSACTION_LIST) {
            tvRiseFall.setText(String.format(getActivity().getResources().getString(R.string.trade_price), NumberUtils.isToCNY() ? "CNY" : "USD"));
        } else {
            tvRiseFall.setText(getActivity().getResources().getString(R.string.rise_fall));
        }

        if (fragmentType != HomeFragment.HOME_RISE_FALL_LIST) {
            tvClose.setText(String.format(getActivity().getResources().getString(R.string.last_prices_cny_usd), NumberUtils.isToCNY() ? "CNY" : "USD"));
        } else {
            tvClose.setText(getActivity().getResources().getString(R.string.last_prices));
        }
    }

    @Override
    public void pollingData() {
        model.getData(fragmentType);
    }

    @Override
    public List<String> getTopics() {
        return null;
    }

    @Override
    public SubscribeCallBack getSubscribeCallBack() {
        return null;
    }

    @Override
    public OnMessageCallBack getOnMessageCallBack() {
        return null;
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return null;
    }

    @Override
    public String getSubscribeKey() {
        return null;
    }

    private void initAdapter() {
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseQuickPageStateAdapter<RiseFallBean, BaseViewHolder>(getActivity(), R.layout.item_rise_fall_list) {
            @Override
            protected void convert(BaseViewHolder helper, RiseFallBean item) {
                helper.setText(R.id.tv_name1, item.getCoin_name());

                if (fragmentType != HomeFragment.HOME_RISE_FALL_LIST) {
                    helper.setText(R.id.tv_last_prices, NumberUtils.transform2CnyOrUsdSymbol(item.getMarket_name(), item.getClose(), item.getMarket_decimals()));
                    if (fragmentType == HomeFragment.HOME_TRANSACTION_LIST) {
                        helper.setText(R.id.tv_name2, "");
                    }
                } else {
                    helper.setText(R.id.tv_last_prices, NumberUtils.getFormatMoney(item.getClose()));
                    helper.setText(R.id.tv_name2, String.format(getResources().getString(R.string.market_name), item.getMarket_name()));
                }

                if (fragmentType == HomeFragment.HOME_TRANSACTION_LIST) {
                    helper.setText(R.id.tv_rise_fall, NumberUtils.formatMoney(item.getTotal_usd()))
                            .setBackgroundRes(R.id.tv_rise_fall, R.drawable.bg_blue_corner)
                            .setTextColor(R.id.tv_rise_fall, getResources().getColor(R.color.theme));
                } else {
                    BigDecimal riseFall = NumberUtils.getRate(item.getClose(), item.getOpen());
                    helper.setText(R.id.tv_rise_fall, NumberUtils.bigDecimal2Percent(riseFall))
                            .setBackgroundRes(R.id.tv_rise_fall, riseFall.compareTo(BigDecimal.ZERO) > 0 ? R.drawable.bg_green_corner : R.drawable.bg_red_corner);
                }
            }
        };
        adapter.setHeaderFooterEmpty(true, true);
        rv.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(RouterMap.K_LINE)
                        .withString(PageConstant.SYMBOL, list.get(position).getSymbol())
                        .navigation();

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.CHANGE_UNIT) {
            updateTitle();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        //加载数据
        model.getData(fragmentType);
        //今日市场情绪
        model.getTodayMarketState();
    }
}
