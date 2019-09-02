package com.fmtch.mine.mvp.view.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.mine.R;
import com.fmtch.mine.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.SELECT_COUNTRY_CODE)
public class SelectCountryCode extends BaseActivity {

    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.rv)
    RecyclerView rv;

    public static final int RESULTCODE = 999;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_activity_select_country_code;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        final List<SuperResponse> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SuperResponse superResponse = new SuperResponse();
            superResponse.setUsername("中国");
            superResponse.setArea("+88");
            list.add(superResponse);
        }
        rv.setLayoutManager(new LinearLayoutManager(this));
        final BaseQuickPageStateAdapter<SuperResponse, BaseViewHolder> adapter = new BaseQuickPageStateAdapter<SuperResponse, BaseViewHolder>(this, R.layout.item_country_code) {
            @Override
            protected void convert(BaseViewHolder helper, SuperResponse item) {
                helper.setText(R.id.tv_name, item.getUsername());
                helper.setText(R.id.tv_code, item.getArea());
            }
        };
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SuperResponse superResponse = (SuperResponse) adapter.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra("area",superResponse.getArea());
                setResult(RESULTCODE,intent);
                finish();
            }
        });
        rv.setAdapter(adapter);
        adapter.showLoadingPage();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setNewData(list);
            }
        }, 2000);
    }

    @OnClick(R2.id.iv_finish)
    public void onIvFinishClicked() {
        finish();
    }

    @OnClick(R2.id.tv_search)
    public void onTvSearchClicked() {

    }
}
