package com.fmtch.module_bourse.ui.home.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.RiseFallBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 交易对涨跌分布
 */
@Route(path = RouterMap.TRADE_RISE_FALL_DISTRIBUTION)
public class RiseFallDistributionActivity extends BaseActivity {

    @BindView(R2.id.tv_coin_kind)
    TextView tvCoinKind;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.rv)
    RecyclerView rv;

    private List<RiseFallBean> list;
    private BaseQuickPageStateAdapter<RiseFallBean, BaseViewHolder> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rise_fall_distribution;
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

        list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            RiseFallBean riseFallBean = new RiseFallBean("EFT" + i, "ETH", "0.00006500", "+ 0.04%");
//            list.add(riseFallBean);
//        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<RiseFallBean, BaseViewHolder>(this, R.layout.item_rise_fall_list,list) {
            @Override
            protected void convert(BaseViewHolder helper, RiseFallBean item) {
//                helper.setText(R.id.tv_name1, item.getName1());
//                helper.setText(R.id.tv_name2, item.getName2());
//                helper.setText(R.id.tv_last_prices, item.getLastPrice());
//                helper.setText(R.id.tv_rise_fall, item.getAmounts());
            }
        };
        rv.setAdapter(adapter);

        View topView = LayoutInflater.from(this).inflate(R.layout.layout_top_activity_rise_fall_distribution, null);
        adapter.addHeaderView(topView);
    }

    @OnClick(R2.id.tv_coin_kind)
    public void onViewClicked() {

    }
}
