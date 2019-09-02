package com.fmtch.module_bourse.ui.trade.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.MyPendOrderBean;
import com.fmtch.module_bourse.ui.trade.model.MyPendOrderModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.MY_PEND_ORDER,extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class MyPendOrderActivity extends BaseActivity {

    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;
    @BindView(R2.id.toolbar)
    public Toolbar toolbar;

    public List<MyPendOrderBean> list;
    public BaseQuickPageStateAdapter<MyPendOrderBean, BaseViewHolder> adapter;
    private MyPendOrderModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_pend_order;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();
        initAdapter();
        model = new MyPendOrderModel(this);
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

    @Override
    protected void initEvent() {
        super.initEvent();
        //自动刷新
        refreshLayout.autoRefresh(400);
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.getData(true);
            }
        });
        //加载更多
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                model.getData(false);
            }
        });
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<MyPendOrderBean, BaseViewHolder>(this, R.layout.item_trade_limit_price) {
            @Override
            protected void convert(BaseViewHolder helper, MyPendOrderBean item) {
                helper.setText(R.id.tv_state, item.getSide().equals("BUY") ? R.string.buy : R.string.sell);
                helper.setBackgroundRes(R.id.tv_state, item.getSide().equals("BUY") ? R.drawable.bg_circle_green : R.drawable.bg_circle_red);
                helper.setText(R.id.tv_name, item.getSymbol());
                String created_at = item.getCreated_at();
                String[] time = created_at.split(" ");
                helper.setText(R.id.tv_time, time[1]);
                helper.setText(R.id.tv_delegation_price, NumberUtils.stripMoneyZeros(item.getPrice()));
                helper.setText(R.id.tv_trade_amounts, NumberUtils.stripMoneyZeros(item.getDeal_number()));
                helper.setText(R.id.tv_delegation_amounts, NumberUtils.stripMoneyZeros(item.getNumber()));
                helper.addOnClickListener(R.id.tv_revoke);
            }
        };
        //点击撤单
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                model.cancelOrder(position);
            }
        });
        rv.setAdapter(adapter);
    }
}
