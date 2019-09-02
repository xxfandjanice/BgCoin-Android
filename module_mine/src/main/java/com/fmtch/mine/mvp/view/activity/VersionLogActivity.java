package com.fmtch.mine.mvp.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.net.API;
import com.fmtch.base.net.RequestUtil;
import com.fmtch.base.net.impl.OnResponseListenerImpl;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import java.util.List;

import butterknife.BindView;

@Route(path = RouterMap.VERSION_LOG)
public class VersionLogActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.rv)
    RecyclerView rv;

    private BaseQuickPageStateAdapter<SuperResponse, BaseViewHolder> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_version_log;
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
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initRv();
        RequestUtil.requestListGet(API.VERSION_LOG + "?app_name=ETF&&mobile_system=1", new OnResponseListenerImpl() {
            @Override
            public void onNextList(List<SuperResponse> responses) {
                super.onNextList(responses);
                adapter.setNewData(responses);
            }
        },this,false);
    }

    private void initRv() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<SuperResponse, BaseViewHolder>(this, R.layout.item_version_log) {
            @Override
            protected void convert(BaseViewHolder helper, SuperResponse item) {
                helper.setText(R.id.tv_version_no, item.getVersion_name())
                        .setText(R.id.tv_update_content, item.getUpgrade_point().replace("\\n", "\n"))
                        .setText(R.id.tv_update_time, item.getTime());
            }
        };
        rv.setAdapter(adapter);
    }

}
