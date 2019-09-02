package com.fmtch.module_bourse.ui.home.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.ClearEditText;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.ui.home.model.ChooseCoinModel;
import com.fmtch.module_bourse.widget.FloatingBarItemDecoration;
import com.fmtch.module_bourse.widget.IndexBar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnTextChanged;

@Route(path = RouterMap.CHOOSE_COIN, extras = PageConstant.LOGIN_NEEDED_CONSTANT)
public class ChooseCoinActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.search)
    ClearEditText search;
    @BindView(R2.id.tv_search)
    TextView tvSearch;
    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.index_bar)
    public IndexBar indexBar;

    @Autowired
    int type;//0:其他（无特殊处理）1:充币 2:提币 3:K线 4:转账币币账户 5:转账法币账户

    @Autowired
    int accpunt_type;//账户类型(总账户0 币币账户1 法币账户2)

    @Autowired
    boolean jump;//是否是跳转

    private LinearLayoutManager mLayoutManager;
    public LinkedHashMap<Integer, String> mHeaderList;
    public ArrayList<AccountBean> coinList;
    public ArrayList<AccountBean> coinListCopy;  //备份数据
    public ArrayList<String> indexList;
    public BaseQuickPageStateAdapter<AccountBean, BaseViewHolder> adapter;

    private ChooseCoinModel model;

    public static final int RESULT_CODE = 888;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_coin;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        accpunt_type = getIntent().getIntExtra(PageConstant.ACCOUNT_TYPE, 0);//账户类型(总账户0 币币账号1 法币账户2)
        type = getIntent().getIntExtra(PageConstant.TYPE, 0);//默认其他（无特殊处理）
        jump = getIntent().getBooleanExtra(PageConstant.JUMP, false);
        mHeaderList = new LinkedHashMap<>();
        coinList = new ArrayList<>();
        coinListCopy = new ArrayList<>();
        indexList = new ArrayList<>();
        model = new ChooseCoinModel(this);
        if (getIntent().getSerializableExtra(PageConstant.COIN_LIST) != null) {
            //上个页面带来数据
            coinList = (ArrayList<AccountBean>) getIntent().getSerializableExtra(PageConstant.COIN_LIST);
            if (coinList == null || coinList.size() < 1)
                model.getCoinList(accpunt_type);
        } else {
            model.getCoinList(accpunt_type);
        }
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
        initAdapter();
    }

    private void initAdapter() {
        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        rv.addItemDecoration(new FloatingBarItemDecoration(this, mHeaderList));
        adapter = new BaseQuickPageStateAdapter<AccountBean, BaseViewHolder>(this, R.layout.item_choose_coin, coinList) {
            @Override
            protected void convert(BaseViewHolder helper, AccountBean item) {
                helper.setText(R.id.tv_name, item.getCoin().getName());
            }
        };
        rv.setAdapter(adapter);

        if (coinList != null && coinList.size() > 0) {
            adapter.setNewData(coinList);
            model.updateHeaderAndIndexBar();
            coinListCopy.addAll(coinList);
        } else {
            adapter.showLoadingPage();
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //右侧 索引 点击监听
        indexBar.setOnTouchingLetterChangedListener(new IndexBar.OnTouchingLetterChangeListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (mHeaderList == null || mHeaderList.size() <= 0) {
                    return;
                }
                for (Integer position : mHeaderList.keySet()) {
                    if (mHeaderList.get(position).equals(s)) {
                        mLayoutManager.scrollToPositionWithOffset(position, 0);
                        return;
                    }
                }
            }

            @Override
            public void onTouchingStart(String s) {
            }

            @Override
            public void onTouchingEnd(String s) {
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (coinList == null || coinList.size() < 1) {
                    return;
                }
                AccountBean accountBean = coinList.get(position);
                Intent intent = new Intent();
                //type  0:其他（无特殊处理）1:充币 2:提币 3:选择币种,选中后进入K线页面
                if (type == 1 && accountBean.getCoin().getCan_recharge() != 1) {
                    //充币但币种不支持充币
                    ToastUtils.showMessage(R.string.coin_not_support_recharge);
                } else if (type == 2 && accountBean.getCoin().getCan_withdraw() != 1) {
                    //提币但币种不支持提币
                    ToastUtils.showMessage(R.string.coin_not_support_withdraw);
                } else if (type == 4 && accountBean.getCoin().getIs_spot() != 1) {
                    //转账不支持币币币交易
                    ToastUtils.showMessage(R.string.coin_not_support_coin_coin_deal);
                } else if (type == 5 && accountBean.getCoin().getIs_otc() != 1) {
                    //转账不支持法币交易
                    ToastUtils.showMessage(R.string.coin_not_support_paris_coin_deal);
                } else {
                    if (jump) {
                        String router = "";
                        switch (type) {
                            case 1:
                                router = RouterMap.RECHARGE_COIN;
                                break;
                            case 2:
                                router = RouterMap.BRING_COIN;
                                break;
                            case 4:
                                router = RouterMap.TRANSFER_COIN;
                                break;
                            case 5:
                                router = RouterMap.TRANSFER_COIN;
                                break;
                        }
                        if (!TextUtils.isEmpty(router)) {
                            ARouter.getInstance().build(router)
                                    .withSerializable(PageConstant.COIN_INFO, accountBean)
                                    .navigation();
                            finish();
                        }
                    } else {
                        intent.putExtra(PageConstant.COIN_INFO, accountBean);
                        setResult(RESULT_CODE, intent);
                        finish();
                    }
                }
            }
        });
    }

    /**
     * 根据用户输入时时搜索
     */
    @OnTextChanged(value = R2.id.search, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onTextChanged(CharSequence s, int start, int before, int count) {
        coinList.clear();
        mHeaderList.clear();
        try {
            //输入特殊字符可能导致crash
            Pattern pattern = Pattern.compile(s.toString(), Pattern.CASE_INSENSITIVE);
            for (AccountBean coinBean : coinListCopy) {
                Matcher matcherName = pattern.matcher(coinBean.getCoin().getName());
                if (matcherName.find()) {
                    coinList.add(coinBean);
                }
            }
            model.updateHeaderAndIndexBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (coinList.size() == 0) {
            adapter.showEmptyPage();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

}
