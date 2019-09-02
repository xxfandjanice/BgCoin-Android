package com.fmtch.module_bourse.ui.property.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.pojo.response.CoinToBTC;
import com.fmtch.base.pojo.response.CoinToUSDT;
import com.fmtch.base.pojo.response.RateInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.DialogUtils;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.AccountDetailBean;
import com.fmtch.module_bourse.ui.property.fragment.PropertyFragment;
import com.fmtch.module_bourse.ui.property.model.AccountDetailModel;
import com.fmtch.module_bourse.utils.RiseFallFormatUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

@Route(path = RouterMap.ACCOUNT_DETAIL)
public class AccountDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.toolbar)
    Toolbar toolbar;
    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;
    @BindView(R2.id.tv_recharge)
    TextView tvRecharge;
    @BindView(R2.id.tv_draw_coin)
    TextView tvDrawCoin;
    @BindView(R2.id.tv_transform)
    TextView tvTransform;
    @BindView(R2.id.tv_trade)
    TextView tvTrade;
    @BindView(R2.id.ll_bottom)
    LinearLayout llBottom;
    private View topView;

    public ArrayList<AccountDetailBean> list;
    public BaseQuickPageStateAdapter<AccountDetailBean, BaseViewHolder> adapter;
    private BottomSheetDialog filterDialog;
    private AccountDetailModel model;
    public int accountType;
    public AccountBean accountBean;

    private RealmList<CoinToBTC> coinToBTCList;
    private RealmList<CoinToUSDT> coinToUSDTList;

    private TextView tvAvailable, tvFreeze, tvConverter, tvConverterType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_detail;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        list = new ArrayList<>();

        RateInfo rateInfo = Realm.getDefaultInstance().where(RateInfo.class).findFirst();
        if (rateInfo != null) {
            coinToBTCList = rateInfo.getCoin_to_btc_list();
            coinToUSDTList = rateInfo.getCoin_to_usdt_list();
        }

        accountBean = (AccountBean) getIntent().getSerializableExtra("detail");
        //账户类型(总账户0 币币账号1 法币账户2)
        accountType = getIntent().getIntExtra("key", -1);
        tvTitle.setText(accountBean.getCoin().getName());
        //币币、法币账户 充币和提币不显示
        if (accountType == PropertyFragment.PROPERTY_COIN_COIN_ACCOUNT || accountType == PropertyFragment.PROPERTY_PARIS_COIN_ACCOUNT) {
            tvRecharge.setVisibility(View.GONE);
            tvDrawCoin.setVisibility(View.GONE);
        } else {
            tvTrade.setVisibility(View.GONE);
        }
        //头布局
        initTopView();
        initAdapter();
        model = new AccountDetailModel(this);
    }

    private void initTopView() {
        topView = LayoutInflater.from(this).inflate(R.layout.layout_account_detail_top, null);
        tvAvailable = topView.findViewById(R.id.tv_available);
        tvFreeze = topView.findViewById(R.id.tv_freeze);
        //折合人民币/美元
        tvConverter = topView.findViewById(R.id.tv_converter);
        tvConverterType = topView.findViewById(R.id.tv_converter_type);
        updateTopView();
    }

    public void updateTopView() {
        tvAvailable.setText(NumberUtils.getFormatMoney(accountBean.getAvailable()));
        tvFreeze.setText(NumberUtils.getFormatMoney(accountBean.getDisabled()));
        if (NumberUtils.isToCNY()) {
            tvConverterType.setText(R.string.converter_coin_cny);
            for (CoinToBTC coinToBTC : coinToBTCList) {
                if (TextUtils.equals(accountBean.getCoin().getName(), coinToBTC.getCoin_name())) {
                    BigDecimal btcDecimal = new BigDecimal(accountBean.getAvailable()).add(new BigDecimal(accountBean.getDisabled()))
                            .multiply(new BigDecimal(coinToBTC.getCoin_to_btc()));
                    tvConverter.setText(NumberUtils.getBTCToMoney(btcDecimal));
                }
            }
        } else {
            tvConverterType.setText(R.string.converter_coin_usd);
            for (CoinToUSDT coinToUSDT : coinToUSDTList) {
                if (TextUtils.equals(accountBean.getCoin().getName(), coinToUSDT.getCoin_name())) {
                    BigDecimal usdtDecimal = new BigDecimal(accountBean.getAvailable()).add(new BigDecimal(accountBean.getDisabled()))
                            .multiply(new BigDecimal(coinToUSDT.getCoin_to_usdt()));
                    tvConverter.setText(usdtDecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString());
                }
            }
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
        //自动刷新
        refreshLayout.autoRefresh(400);
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.getData(true, "");
            }
        });
        //加载更多
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                model.getData(false, "");
            }
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //recycleView滑动监听
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //滑动停止
                    llBottom.animate().translationY(0);
                } else {
                    llBottom.animate().translationY(llBottom.getHeight());
                }
            }
        });
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseQuickPageStateAdapter<AccountDetailBean, BaseViewHolder>(this, R.layout.item_account_detail) {
            @Override
            protected void convert(BaseViewHolder helper, AccountDetailBean item) {
                helper.setText(R.id.tv_name, item.getOp_name());
                if (!TextUtils.isEmpty(item.getAvailable_number())) {
                    if (Double.parseDouble(item.getAvailable_number()) > 0) {
                        helper.setText(R.id.tv_available_number, "+" + item.getAvailable_number());
                    } else {
                        helper.setText(R.id.tv_available_number, item.getAvailable_number());
                    }
                }
                helper.setText(R.id.tv_available_after, item.getAvailable_after());

                if (!TextUtils.isEmpty(item.getDisabled_number())) {
                    if (Double.parseDouble(item.getDisabled_number()) > 0) {
                        helper.setText(R.id.tv_disabled_number, "+" + item.getDisabled_number());
                    } else {
                        helper.setText(R.id.tv_disabled_number, item.getDisabled_number());
                    }
                }
                helper.setText(R.id.tv_disabled_after, item.getDisabled_after());
                String time = RiseFallFormatUtils.formatTime2(item.getCreated_at());
                helper.setText(R.id.tv_time, time);
            }
        };
        adapter.setHeaderAndEmpty(true);
        rv.setAdapter(adapter);
        adapter.addHeaderView(topView);
//        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                ARouter.getInstance().build(RouterMap.TRANSFER_DETAIL)
//                        .withString(PageConstant.COIN_NAME, accountBean.getCoin().getName())
//                        .withSerializable(PageConstant.TRANSFER_INFO, list.get(position))
//                        .navigation();
//            }
//        });
    }

    @OnClick({R2.id.iv_filter, R2.id.tv_recharge, R2.id.tv_draw_coin, R2.id.tv_transform, R2.id.tv_trade})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.iv_filter) {
            showFilterDialog();
        } else if (id == R.id.tv_recharge) {
            if (accountBean.getCoin().getCan_recharge() != 1) {
                ToastUtils.showMessage(R.string.coin_not_support_recharge);
            } else {
                ARouter.getInstance().build(RouterMap.RECHARGE_COIN)
                        .withSerializable(PageConstant.COIN_INFO, accountBean)
                        .navigation();
            }
        } else if (id == R.id.tv_draw_coin) {
            //提币
            UserLoginInfo userLoginInfo = Realm.getDefaultInstance().where(UserLoginInfo.class).findFirst();
            if (userLoginInfo == null) {
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
            } else if (userLoginInfo.getKyc_status() == 0) {
                //未实名认证
                CommonDialog dialog = new CommonDialog(this);
                dialog.showMsg(getString(R.string.no_auth), false);
                dialog.setBtnConfirmText(getString(R.string.go_auth));
                dialog.show();
                dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                    @Override
                    public void onConfirmClick() {
                        ARouter.getInstance().build(RouterMap.AUTH).navigation();
                    }
                });
            } else if (userLoginInfo.getKyc_status() == 1) {
                //已经实名认证
                if (accountBean.getCoin().getCan_withdraw() != 1) {
                    ToastUtils.showMessage(R.string.coin_not_support_withdraw);
                } else {
                    ARouter.getInstance().build(RouterMap.BRING_COIN)
                            .withSerializable(PageConstant.COIN_INFO, accountBean)
                            .navigation();
                }
            } else {
                //申请实名认证进入实名认证状态
                ARouter.getInstance().build(RouterMap.AUTH_STATUS).navigation();
            }
        } else if (id == R.id.tv_transform) {
            ARouter.getInstance().build(RouterMap.TRANSFER_COIN)
                    .withSerializable(PageConstant.COIN_INFO, accountBean)
                    .withInt(PageConstant.ACCOUNT_TYPE, accountType)
                    .navigation();
        } else if (id == R.id.tv_trade) {
            ARouter.getInstance().build(RouterMap.MAIN_PAGE)
                    .withInt(PageConstant.PAGE_INDEX, 2)
                    .navigation();
        }
    }

    /**
     * 筛选dialog
     */
    public void showFilterDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_account_detail, null);
        view.findViewById(R.id.tv_all).setOnClickListener(this);
        View tvDrawCoin = view.findViewById(R.id.tv_draw_coin);
        tvDrawCoin.setOnClickListener(this);
        View tvRecharge = view.findViewById(R.id.tv_recharge);
        tvRecharge.setOnClickListener(this);
        View tvSystem = view.findViewById(R.id.tv_system);
        tvSystem.setOnClickListener(this);
        view.findViewById(R.id.tv_turn_into).setOnClickListener(this);
        view.findViewById(R.id.tv_roll_out).setOnClickListener(this);
        //币币或法币页面 充币、提币不显示
        if (accountType == PropertyFragment.PROPERTY_COIN_COIN_ACCOUNT || accountType == PropertyFragment.PROPERTY_PARIS_COIN_ACCOUNT) {
            tvDrawCoin.setVisibility(View.GONE);
            tvRecharge.setVisibility(View.GONE);
            tvSystem.setVisibility(View.GONE);
        }
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        filterDialog = DialogUtils.showDetailBottomDialog(this, view);
        filterDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //账户类型(总账户0 币币账号1 法币账户2)
        //总账户 操作类型: 1：充币 2：提币 3：转入 4：转出   5：系统
        //币币账户 操作类型：1：转入 2：转出 3：买入挂单 4：买入撤销 5：卖出挂单 6：卖出撤销 7：买入收入 8：买入支出 9：卖出收入 10：卖出支出 11：差价返还
        //法币账户 操作类型：1：转入 2：转出 3：卖出挂单 4：卖出撤销 5：手续费返还 6：普通卖出 7：取消订单 8：卖出成交 9：卖出手续费 10：买入成交 11：买入手续费
        if (id == R.id.tv_all) {
            refreshLayout.autoRefreshAnimationOnly();
            model.getData(true, "");
        } else if (id == R.id.tv_draw_coin) {
            refreshLayout.autoRefreshAnimationOnly();
            model.getData(true, "2");
        } else if (id == R.id.tv_recharge) {
            refreshLayout.autoRefreshAnimationOnly();
            model.getData(true, "1");
        } else if (id == R.id.tv_turn_into) {
            refreshLayout.autoRefreshAnimationOnly();
            switch (accountType){
                case 0:
                    //总账户
                    model.getData(true, "3");
                    break;
                case 1:
                    //币币账号
                    model.getData(true, "1");
                    break;
                case 2:
                    //法币账户
                    model.getData(true, "1");
                    break;
            }
        } else if (id == R.id.tv_roll_out) {
            refreshLayout.autoRefreshAnimationOnly();
            switch (accountType){
                case 0:
                    //总账户
                    model.getData(true, "4");
                    break;
                case 1:
                    //币币账号
                    model.getData(true, "2");
                    break;
                case 2:
                    //法币账户
                    model.getData(true, "2");
                    break;
            }
        } else if (id == R.id.tv_system) {
            refreshLayout.autoRefreshAnimationOnly();
            model.getData(true, "5");
        }
        filterDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountBean != null && model != null) {
            model.getCoinInfo(accountType, accountBean.getCoin_id());
        }
    }
}
