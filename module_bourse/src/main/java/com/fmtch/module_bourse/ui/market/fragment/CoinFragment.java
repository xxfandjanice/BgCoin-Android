package com.fmtch.module_bourse.ui.market.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.MyChooseBean;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.Bourse_MainActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.listener.DataListener;
import com.fmtch.module_bourse.ui.market.model.CoinModel;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.icechao.klinelib.utils.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by wtc on 2019/5/11
 */
public class CoinFragment extends LazyBaseFragment {
    @BindView(R2.id.rv)
    RecyclerView rv;
    public List<MarketBean> list;
    public BaseQuickPageStateAdapter<MarketBean, BaseViewHolder> adapter;
    private int fragmentType = -1;
    public int sortType = MarketFragment.SORT_NORMAL;   //列表排序方式
    private CoinModel coinModel;
    public RealmResults<SymbolBean> myChooseAsyncList;

    public static CoinFragment newInstance(Bundle bundle) {
        CoinFragment fragment = new CoinFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_market_coin;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        fragmentType = bundle.getInt(FRAGMENT_KEY);
        list = new ArrayList<>();
        initAdapter();
        //获取已加入自选的交易对
        updateMyChoose();
        coinModel = new CoinModel(this);
        MarketFragment marketFragment = ((Bourse_MainActivity) getActivity()).getMarketFragment();
        marketFragment.setDataListener(coinModel.getDataListener(fragmentType));
        marketFragment.setDataChangeListener(coinModel.getDataChangeListener());
    }


    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseQuickPageStateAdapter<MarketBean, BaseViewHolder>(getActivity(), R.layout.item_market,list) {
            @Override
            protected void convert(BaseViewHolder helper, MarketBean item) {
                helper.setText(R.id.tv_name1, item.getCoin_name());
                helper.setText(R.id.tv_name2, item.getMarket_name());
                helper.setText(R.id.tv_last_prices, NumberUtils.getFormatMoney(item.getClose()));
                helper.setText(R.id.tv_trade_amount, String.format(getString(R.string.trade_amount), NumberUtils.getFormatMoney(item.getNumber(), 2)));
                helper.setText(R.id.tv_unit, NumberUtils.transform2CnyOrUsdSymbol(item.getMarket_name(), item.getClose()));
                BigDecimal riseFall = NumberUtils.getRate(item.getClose(), item.getOpen());
                helper.setText(R.id.tv_rise_fall, NumberUtils.bigDecimal2Percent(riseFall))
                        .setBackgroundRes(R.id.tv_rise_fall, riseFall.compareTo(BigDecimal.ZERO) > 0 ? R.drawable.bg_green_corner : R.drawable.bg_red_corner);
            }
        };
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(RouterMap.K_LINE)
                        .withString(PageConstant.SYMBOL, list.get(position).getSymbol())
                        .navigation();
            }
        });
    }

    //设置排序方式
    public void setSortType(int sortType) {
        this.sortType = sortType;
        if (list != null && list.size() > 0) {
            coinModel.updateListBySort(sortType);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (fragmentType == MarketFragment.MARKET_CHOOSE) {
            coinModel.filterData(getString(R.string.optional));
        }
    }

    private void updateMyChoose() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                myChooseAsyncList = realm.where(SymbolBean.class).findAll();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.CHANGE_UNIT) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
