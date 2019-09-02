package com.fmtch.module_bourse.ui.trade.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.ParisOrderStateAdapter;
import com.fmtch.module_bourse.base.LazyBaseFragment;
import com.fmtch.module_bourse.bean.ParisOrderStateBean;
import com.fmtch.module_bourse.ui.trade.activity.ParisCoinOrderActivity;
import com.fmtch.module_bourse.ui.trade.model.ParisOrderStateModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by wtc on 2019/6/28
 */
public class ParisCoinOrderStateFragment extends LazyBaseFragment {
    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;

    public List<ParisOrderStateBean> list;
    public ParisOrderStateAdapter adapter;
    private ParisOrderStateModel model;
    public int orderStateType;

    public static ParisCoinOrderStateFragment newInstance(Bundle bundle) {
        ParisCoinOrderStateFragment fragment = new ParisCoinOrderStateFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_paris_coin_order_state;
    }

    @Override
    protected void initData() {
        super.initData();
        orderStateType = getArguments().getInt(PageConstant.KEY, -1);
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ParisOrderStateAdapter(getActivity(), R.layout.item_paris_order_state);
        rv.setAdapter(adapter);

        model = new ParisOrderStateModel(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
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
        //进入详情
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ToastUtils.isFastClick())
                    return;
                ParisOrderStateBean stateBean = list.get(position);
                String path = "";
                if (orderStateType == ParisCoinOrderActivity.UNFINISHED) {
                    if (stateBean.getSide().equals("BUY")) {
                        path = RouterMap.PARIS_BUY_ORDER;
                    } else if (stateBean.getSide().equals("SELL")) {
                        path = RouterMap.PARIS_SELL_ORDER;
                    }
                } else if (orderStateType == ParisCoinOrderActivity.FINISHED || orderStateType == ParisCoinOrderActivity.CANCEL) {
                    path = RouterMap.PARIS_COIN_ORDER_DETAIL;
                }
                ARouter.getInstance().build(path)
                        .withInt(PageConstant.ID, stateBean.getId())
                        .navigation();
            }
        });
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        adapter.showLoadingPage();
        model.getData(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.closeCountDown();
    }
}
