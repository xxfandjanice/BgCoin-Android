package com.fmtch.module_bourse.ui.home.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.DeepAdapter;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.bean.DeepTransformBean;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.model.DeepModel;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;

/**
 * Created by wtc on 2019/5/17
 * K线图页面-深度
 */
public class DeepFragment extends LazyReFreshDataBaseFragment {
    @BindView(R2.id.tv_num_fir)
    TextView tvNumFir;
    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.tv_num_sec)
    TextView tvNumSec;
    @BindView(R2.id.rv)
    RecyclerView rv;

    public List<DeepTransformBean> list = new ArrayList<>();
    private DeepModel model;
    public BaseQuickPageStateAdapter<DeepTransformBean, BaseViewHolder> adapter;
    public String symbol;
    public boolean depthSubscribeResult = false;   //深度是否订阅成功
    private ArrayList<String> topics = new ArrayList<>();
    public int coin_decimals = -1;                    //币种保留小数位
    public int market_decimal = -1;                  //价格保留小数位
    public int depthDataSize = 35;
    public String depthTopic;

    public static DeepFragment newInstance(Bundle bundle) {
        DeepFragment fragment = new DeepFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_deep;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            symbol = bundle.getString(PageConstant.SYMBOL);
            if (!TextUtils.isEmpty(symbol)) {
                String[] split = symbol.split("/");
                tvNumFir.setText(String.format(getResources().getString(R.string.num_bracket), split[0]));
                tvNumSec.setText(String.format(getResources().getString(R.string.num_bracket), split[0]));
                tvPrice.setText(String.format(getResources().getString(R.string.price_bracket), split[1]));
                SymbolBean symbol = Realm.getDefaultInstance().where(SymbolBean.class).equalTo("symbol", this.symbol).findFirst();
                if (symbol != null) {
                    market_decimal = symbol.getPrice_decimals();
                    coin_decimals = symbol.getNumber_decimals();
                }
            }
        }
        //添加订阅主题
        depthTopic = String.format(TopicType.DEPTH, symbol, depthDataSize);
        topics.add(depthTopic);
        model = new DeepModel(this);
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
    public void onFragmentResume() {
        super.onFragmentResume();
        model.updateUI();
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DeepAdapter(getActivity(), R.layout.item_deep);
        rv.setAdapter(adapter);
    }
}
