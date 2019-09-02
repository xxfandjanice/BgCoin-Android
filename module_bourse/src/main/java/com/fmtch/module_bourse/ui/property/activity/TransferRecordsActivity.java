package com.fmtch.module_bourse.ui.property.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.ui.property.model.TransferRecordsModel;
import com.fmtch.module_bourse.utils.RiseFallFormatUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.TRANSFER_COIN_RECORDS)
public class TransferRecordsActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;
    @BindView(R2.id.rv)
    RecyclerView rv;

    @Autowired
    int type; // 1：充币    2：提币   3：划转
    @Autowired
    public String account_type;//类型（ account：总账户划转记录 spot_account：币币账户划转记录 otc_account：法币账户划转记录）

    public List<AccountDetailBean> list;
    public BaseQuickPageStateAdapter<AccountDetailBean, BaseViewHolder> adapter;

    public ArrayList<AccountBean> coinList;
    public AccountBean coinInfo;


    private TransferRecordsModel model;

    private static final int REQUEST_CODE_COIN = 111;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_coin_records;
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
        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.getRecords(true, coinInfo.getCoin_id());
            }
        });
        //加载更多
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                model.getRecords(false, coinInfo.getCoin_id());
            }
        });
        // 1：充币    2：提币   3：划转
        switch (type){
            case 1:
                tvTitle.setText(R.string.recharge_coin_records);
                break;
            case 2:
                tvTitle.setText(R.string.bring_coin_records);
                break;
            case 3:
                tvTitle.setText(R.string.transfer_coin_records);
                break;
        }
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        type = getIntent().getIntExtra(PageConstant.TYPE, 3);// 默认划转
        account_type = getIntent().getStringExtra(PageConstant.ACCOUNT_TYPE);
        initAdapter();
        list = new ArrayList<>();
        model = new TransferRecordsModel(this, type);
        if (getIntent().getSerializableExtra(PageConstant.COIN_LIST) != null)
            coinList = (ArrayList<AccountBean>) getIntent().getSerializableExtra(PageConstant.COIN_LIST);

        if (getIntent().getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) getIntent().getSerializableExtra(PageConstant.COIN_INFO);
            model.getRecords(true, coinInfo.getCoin_id());
        }

    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<AccountDetailBean, BaseViewHolder>(this, R.layout.item_transfer_records) {
            @Override
            protected void convert(BaseViewHolder helper, AccountDetailBean item) {
                // 1：充币    2：提币   3：划转
                String time = "";
                switch (type){
                    case 1:
                        helper.setText(R.id.tv_name, R.string.drawer_recharge);
                        //充值状态: 0:确认中 1:已完成
                        if (NumberUtils.equalsInteger(item.getStatus(),0)){
                            helper.setText(R.id.tv_state, R.string.sure_ing);
                        }else if (NumberUtils.equalsInteger(item.getStatus(),1)){
                            helper.setText(R.id.tv_state, R.string.done);
                        }
                        time = RiseFallFormatUtils.formatTime(item.getCreated_at());
                        break;
                    case 2:
                        helper.setText(R.id.tv_name, R.string.drawer_draw_coin);
                        // 提现状态: 0:审核中 1:已完成 2:已撤销  3和4: 处理中
                        if (NumberUtils.equalsInteger(item.getStatus(),0)){
                            helper.setText(R.id.tv_state, R.string.checking);
                        }else if (NumberUtils.equalsInteger(item.getStatus(),1)){
                            helper.setText(R.id.tv_state, R.string.done);
                        }else if (NumberUtils.equalsInteger(item.getStatus(),2)){
                            helper.setText(R.id.tv_state, R.string.canceled_);
                        }else {
                            helper.setText(R.id.tv_state, R.string.deal_ing);
                        }
                        time = RiseFallFormatUtils.formatTime(item.getCreated_at());
                        break;
                    case 3:
                        helper.setText(R.id.tv_name, item.getType_text());
                        helper.setText(R.id.tv_state, R.string.success);
                        time = RiseFallFormatUtils.formatTime(item.getTime());
                        break;
                }
                helper.setText(R.id.tv_amounts, NumberUtils.getFormatMoney(item.getNumber()));
                helper.setText(R.id.tv_time, time);
            }
        };
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouter.getInstance().build(RouterMap.TRANSFER_DETAIL)
                        .withInt(PageConstant.TYPE, type)
                        .withString(PageConstant.ACCOUNT_TYPE,account_type)
                        .withString(PageConstant.COIN_NAME, coinInfo.getCoin().getName())
                        .withSerializable(PageConstant.TRANSFER_INFO, list.get(position))
                        .navigation();
            }
        });
    }

    @OnClick(R2.id.iv_filter)
    public void onViewClicked() {
        ARouter.getInstance().build(RouterMap.CHOOSE_COIN)
                .withInt(PageConstant.TYPE, 0)
                .withSerializable(PageConstant.COIN_LIST, coinList)
                .navigation(this, REQUEST_CODE_COIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_COIN && data != null && data.getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) data.getSerializableExtra(PageConstant.COIN_INFO);
            model.getRecords(true, coinInfo.getCoin_id());
        }
    }

}
