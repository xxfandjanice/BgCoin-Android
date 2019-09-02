package com.fmtch.module_bourse.ui.trade.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.widget.dialog.CustomDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.DelegationOrderAdapter;
import com.fmtch.module_bourse.bean.DelegationOrderBean;
import com.fmtch.module_bourse.ui.trade.model.DelegationOrderModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.DELEGATION_ORDER, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class DelegationOrderActivity extends BaseActivity {

    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;
    @BindView(R2.id.toolbar)
    public Toolbar toolbar;
    @BindView(R2.id.iv_switch)
    public ImageView ivSwitch;
    public boolean isPauseAll;

    public List<DelegationOrderBean> list;
    public BaseQuickPageStateAdapter<DelegationOrderBean, BaseViewHolder> adapter;
    private DelegationOrderModel model;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_delegation_order;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();
        initAdapter();
        model = new DelegationOrderModel(this);
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
                model.getPauseSwitchState();
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
        adapter = new DelegationOrderAdapter(this, R.layout.item_delegation_order);
        rv.setAdapter(adapter);
        //点击撤销
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                new CustomDialog(DelegationOrderActivity.this)
                        .setContent(R.string.sure_cancel_order)
                        .setSubmitListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                model.cancelOrder(position);
                            }
                        })
                        .builder()
                        .show();
            }
        });
        //进入详情
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(RouterMap.DELEGATION_ORDER_DETAIL)
                        .withSerializable(PageConstant.DELEGATION_ORDER_DETAIL, list.get(position))
                        .navigation();
            }
        });
    }

    @OnClick(R2.id.iv_switch)
    public void onViewClicked() {
        //全部暂停
        new CustomDialog(DelegationOrderActivity.this)
                .setContent(isPauseAll ? R.string.sure_release_order : R.string.sure_pause_order)
                .setSubmitListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.pauseOrder();
                    }
                })
                .builder()
                .show();
    }
}
