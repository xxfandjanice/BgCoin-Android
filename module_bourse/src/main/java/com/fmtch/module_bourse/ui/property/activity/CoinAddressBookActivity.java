package com.fmtch.module_bourse.ui.property.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.CoinAddressBean;
import com.fmtch.module_bourse.ui.property.model.CoinAddressBookModel;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterMap.COIN_ADDRESS_BOOK)
public class CoinAddressBookActivity extends BaseActivity {


    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.rv)
    RecyclerView rv;

    public AccountBean coinInfo;
    public CoinAddressBean SelecCoinAddressBean;
    private CoinAddressBookModel model;

    public BaseQuickPageStateAdapter<CoinAddressBean, BaseViewHolder> adapter;

    public static final int RESULT_CODE = 1000;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_coin_address_book;
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
        if (coinInfo != null && coinInfo.getCoin() != null && !TextUtils.isEmpty(coinInfo.getCoin().getName()))
            tvTitle.setText(String.format(getString(R.string.coin_address), coinInfo.getCoin().getName()));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initRv();
        model = new CoinAddressBookModel(this);
        if (getIntent().getSerializableExtra(PageConstant.COIN_INFO) != null) {
            coinInfo = (AccountBean) getIntent().getSerializableExtra(PageConstant.COIN_INFO);
            model.getCoinAddressList(coinInfo.getCoin_id());
        }
        if (getIntent().getSerializableExtra(PageConstant.COIN_ADDRESS_INFO) != null) {
            SelecCoinAddressBean = (CoinAddressBean) getIntent().getSerializableExtra(PageConstant.COIN_ADDRESS_INFO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (coinInfo != null && model != null) {
            model.getCoinAddressList(coinInfo.getCoin_id());
        }
    }

    private void initRv() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<CoinAddressBean, BaseViewHolder>(this, R.layout.item_address_book) {
            @Override
            protected void convert(BaseViewHolder helper, CoinAddressBean item) {
                helper.setText(R.id.tv_note, item.getNote())
                        .setText(R.id.tv_address, item.getAddress())
                        .setText(R.id.tv_coin_name, coinInfo.getCoin().getName())
                        .setImageResource(R.id.iv_select, item.getType() == 1 ? R.drawable.ic_checked : R.drawable.ic_unchecked)
                        .addOnClickListener(R.id.content)
                        .addOnClickListener(R.id.view_delete);
            }
        };
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                List<CoinAddressBean> datas = (List<CoinAddressBean>) adapter.getData();
                if (datas == null || datas.size() < 1 || datas.get(position) == null)
                    return;
                CoinAddressBean coinAddressBean = datas.get(position);
                if (view.getId() == R.id.content) {
//                    for (int i = 0; i < datas.size(); i++) {
//                        if (i == position) {
//                            datas.get(i).setType(1);
//                        } else {
//                            datas.get(i).setType(0);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent();
                    intent.putExtra(PageConstant.COIN_ADDRESS_INFO, coinAddressBean);
                    setResult(RESULT_CODE, intent);
                    finish();
                } else if (view.getId() == R.id.view_delete) {
                    model.deleteCoinAddress(coinAddressBean.getId(), position);
                }
            }
        });
        rv.setAdapter(adapter);
    }

    //添加地址
    @OnClick(R2.id.tv_add_address)
    public void onViewClicked() {
        if (coinInfo != null) {
            ARouter.getInstance().build(RouterMap.ADD_COIN_ADDRESS)
                    .withSerializable(PageConstant.COIN_INFO, coinInfo)
                    .navigation();
        }
    }
}
