package com.fmtch.module_bourse.ui.home.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.bean.DealOkBean;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.model.DealOkModel;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

/**
 * Created by wtc on 2019/5/17
 * K线图-成交页面
 */
public class DealOkFragment extends LazyReFreshDataBaseFragment {


    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.tv_amounts)
    TextView tvAmounts;
    @BindView(R2.id.rv)
    RecyclerView rv;

    public List<DealOkBean> list = new ArrayList<>();
    private DealOkModel model;
    public BaseQuickPageStateAdapter<DealOkBean, BaseViewHolder> adapter;
    public String symbol;
    private ArrayList<String> topics = new ArrayList<>();
    public boolean tradeSubscribeResult = false;   //交易数据是否订阅成功
    public int coin_decimals = -1;                    //币种保留小数位
    public int market_decimal = -1;                  //价格保留小数位
    public String dealOkTopic;

    public static DealOkFragment newInstance(Bundle bundle) {
        DealOkFragment fragment = new DealOkFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_deal_ok;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            symbol = bundle.getString(PageConstant.SYMBOL);
            String[] split = symbol.split("/");
            tvAmounts.setText(String.format(getResources().getString(R.string.num_bracket), split[0]));
            tvPrice.setText(String.format(getResources().getString(R.string.price_bracket), split[1]));
            SymbolBean symbol = Realm.getDefaultInstance().where(SymbolBean.class).equalTo("symbol", this.symbol).findFirst();
            if (symbol != null) {
                market_decimal = symbol.getPrice_decimals();
                coin_decimals = symbol.getNumber_decimals();
            }
        }
        dealOkTopic = TopicType.TRADE + symbol;
        topics.add(dealOkTopic);
        model = new DealOkModel(this);
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
    public String getSubscribeKey() {
        return this.getClass().getSimpleName();
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return model.getSocketConnectListener();
    }

    @Override
    protected void initView() {
        super.initView();
        initAdapter();
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        model.getData();
    }

    @Override
    public void onFragmentResume(boolean firstResume) {
        super.onFragmentResume(firstResume);
        model.updateUI();
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseQuickPageStateAdapter<DealOkBean, BaseViewHolder>(getActivity(), R.layout.item_deal_ok, list) {
            @Override
            protected void convert(BaseViewHolder helper, DealOkBean item) {
                String created_at = item.getCreated_at();
                String[] time = created_at.split(" ");
                helper.setText(R.id.tv_time, time[1]);
                helper.setText(R.id.tv_orientation, item.getSide().equals("BUY") ? R.string.buy_in : R.string.sell_out);
                helper.setTextColor(R.id.tv_orientation, item.getSide().equals("BUY") ? getActivity().getResources().getColor(R.color.cl_03c087) : getActivity().getResources().getColor(R.color.cl_f55758));
                helper.setText(R.id.tv_price, market_decimal == -1 ? NumberUtils.stripMoneyZeros(item.getPrice()) : NumberUtils.setScale(item.getPrice(), market_decimal));
                helper.setText(R.id.tv_amounts, coin_decimals == -1 ? NumberUtils.stripMoneyZeros(item.getNumber()):NumberUtils.setScale(item.getNumber(),coin_decimals));
            }
        };
        rv.setAdapter(adapter);
    }
}
