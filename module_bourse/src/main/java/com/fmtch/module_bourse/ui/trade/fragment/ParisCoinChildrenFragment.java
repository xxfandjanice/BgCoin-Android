package com.fmtch.module_bourse.ui.trade.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.adapter.BaseQuickPageStateAdapter;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.imageloader.CircleTransformation;
import com.fmtch.base.pojo.UserLoginInfo;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.GlideLoadUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.base.utils.ToastUtils;
import com.fmtch.base.utils.UserInfoIntercept;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.bean.ParisCoinBean;
import com.fmtch.module_bourse.bean.ParisCoinMarketBean;
import com.fmtch.module_bourse.event.ParisCoinFilterEvent;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.trade.model.ParisCoinChildrenModel;
import com.fmtch.module_bourse.utils.AppUtils;
import com.fmtch.module_bourse.utils.SizeUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by wtc on 2019/6/25
 */
public class ParisCoinChildrenFragment extends LazyReFreshDataBaseFragment {

    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.iv_add)
    ImageView ivAdd;
    @BindView(R2.id.refreshLayout)
    public SmartRefreshLayout refreshLayout;

    public List<ParisCoinMarketBean> list;
    public BaseQuickPageStateAdapter<ParisCoinMarketBean, BaseViewHolder> adapter;
    private ParisCoinChildrenModel model;
    public String tradeType = ParisCoinParentFragment.PARIS_TRADE_BUY;  //出售或购买(默认购买)
    public ParisCoinBean coinBean;
    private boolean init = false;
    private String min = "";
    private String payMethods = "";


    public static ParisCoinChildrenFragment newInstance(Bundle bundle) {
        ParisCoinChildrenFragment fragment = new ParisCoinChildrenFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_children_paris_coin;
    }

    @Override
    public void pollingData() {
        model.getData(true, min, payMethods);
    }

    @Override
    public List<String> getTopics() {
        return null;
    }

    @Override
    public SubscribeCallBack getSubscribeCallBack() {
        return null;
    }

    @Override
    public OnMessageCallBack getOnMessageCallBack() {
        return null;
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return null;
    }

    @Override
    public String getSubscribeKey() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void initData() {
        super.initData();
        list = new ArrayList<>();
        model = new ParisCoinChildrenModel(this);
        coinBean = (ParisCoinBean) getArguments().getSerializable(PageConstant.KEY);
        initAdapter();
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseQuickPageStateAdapter<ParisCoinMarketBean, BaseViewHolder>(getActivity(), R.layout.item_paris_coin, list) {
            @Override
            protected void convert(BaseViewHolder helper, ParisCoinMarketBean item) {
                helper.addOnClickListener(R.id.tv_buy_sell);
                helper.addOnClickListener(R.id.iv_avatar);
                helper.addOnClickListener(R.id.tv_name);
                if (tradeType.equals(ParisCoinParentFragment.PARIS_TRADE_BUY)) {
                    helper.setText(R.id.tv_buy_sell, R.string.purchase)
                            .setBackgroundRes(R.id.tv_buy_sell, R.drawable.shape_login_btn_blue)
                            .setTextColor(R.id.tv_price, getResources().getColor(R.color.theme));
                } else if (tradeType.equals(ParisCoinParentFragment.PARIS_TRADE_SELL)) {
                    helper.setText(R.id.tv_buy_sell, R.string.sale)
                            .setBackgroundRes(R.id.tv_buy_sell, R.drawable.shape_sell_btn_red)
                            .setTextColor(R.id.tv_price, getResources().getColor(R.color.cl_f55758));
                }
                GlideLoadUtils.getInstance().glideLoad(getActivity(), item.getAvatar(), (ImageView) helper.getView(R.id.iv_avatar), new CircleTransformation(getContext()), R.drawable.icon_default_avatar);
                helper.setText(R.id.tv_name, item.getUsername());
                helper.setText(R.id.tv_trade_amount, String.format(getResources().getString(R.string.paris_coin_trade_amount), item.getOrder_count()));
                helper.setText(R.id.tv_trade_rate, String.format(getResources().getString(R.string.paris_coin_trade_rate), item.getFinish_percent()));
                helper.setText(R.id.tv_price, "￥" + item.getPrice());
                String symbol = item.getSymbol();
                String[] split = symbol.split("/");
                helper.setText(R.id.tv_limit_money, item.getMin_cny() + "-" + item.getMax_cny() + " " + split[1]);
                helper.setText(R.id.tv_amount, item.getNumber() + " " + split[0]);
                helper.setGone(R.id.iv_pay_bank_card, item.getBank() == 1)
                        .setGone(R.id.iv_pay_wechat, item.getWechat() == 1)
                        .setGone(R.id.iv_pay_zfb, item.getAlipay() == 1);
            }
        };
        rv.setAdapter(adapter);
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        init = true;
        adapter.showLoadingPage();
        model.getData(true, min, payMethods);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        //刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                model.getData(true, min, payMethods);
            }
        });
        //加载更多
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                model.getData(false, min, payMethods);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (ToastUtils.isFastClick())
                    return;
                ParisCoinMarketBean item = list.get(position);
                if (view.getId() == R.id.tv_buy_sell) {
                    //创建订单
                    if (UserInfoIntercept.userBindMobile(getActivity()) && UserInfoIntercept.userAuth(getActivity())) {
                        //已经绑定手机号并实名认证
                        if (item == null)
                            return;
                        if (tradeType.equals(ParisCoinParentFragment.PARIS_TRADE_BUY)) {
                            ARouter.getInstance().build(RouterMap.PARIS_BUY)
                                    .withInt(PageConstant.ID, item.getId())
                                    .withInt(PageConstant.SCALE, coinBean.getOtc_num_decimals())
                                    .navigation();
                        } else if (tradeType.equals(ParisCoinParentFragment.PARIS_TRADE_SELL)) {
                            if (UserInfoIntercept.havePayment(getActivity())) {
                                //用户设置收付款方式
                                ARouter.getInstance().build(RouterMap.PARIS_SELL)
                                        .withInt(PageConstant.ID, item.getId())
                                        .withInt(PageConstant.SCALE, coinBean.getOtc_num_decimals())
                                        .navigation();
                            }
                        }
                    }
                } else if ((view.getId() == R.id.iv_avatar || view.getId() == R.id.tv_name)) {
                    //点击头像进去个人信息
                    ARouter.getInstance().build(RouterMap.MERCHANT_HOMEPAGE)
                            .withInt(PageConstant.ID, item.getUser_id())
                            .navigation();
                }
            }
        });
        //recycleView滑动监听
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ivAdd.animate().translationY(0);
                } else {
                    ivAdd.animate().translationY(SizeUtils.dp2px(30) + ivAdd.getHeight());
                }
            }
        });
    }


    @OnClick(R2.id.iv_add)
    public void onViewClicked() {
        //判断是否设置用户名及设置收付款方式
        if (UserInfoIntercept.haveUserName(getActivity()) && UserInfoIntercept.havePayment(getActivity())) {
            int type = -1;
            if (tradeType.equals(ParisCoinParentFragment.PARIS_TRADE_BUY)) {
                //购买
                type = 0;
            } else if (tradeType.equals(ParisCoinParentFragment.PARIS_TRADE_SELL)) {
                //出售
                type = 1;
            }
            ARouter.getInstance().build(RouterMap.PUBLISH_DELEGATION_ORDER)
                    .withSerializable(PageConstant.COIN, coinBean)
                    .withInt(PageConstant.PARIS_TRADE_TYPE, type)
                    .navigation();
        }
    }

    /**
     * 切换买卖
     *
     * @param tradeType
     */
    public void switchBuyOrSell(String tradeType) {
        this.tradeType = tradeType;
        if (init) {
            model.getData(true, min, payMethods);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        //条件筛选
        if (event.getTag() == EventType.PARIS_COIN_FILTER) {
            ParisCoinFilterEvent data = (ParisCoinFilterEvent) event.getData();
            String minMoney = data.getMinMoney();
            List<String> selectedPayMethods = data.getSelectedPayMethods();

            StringBuilder selectedPay = new StringBuilder();
            if (selectedPayMethods != null && selectedPayMethods.size() > 0) {
                for (int i = 0; i < selectedPayMethods.size(); i++) {
                    if (i == 0) {
                        selectedPay.append(selectedPayMethods.get(0));
                    } else {
                        selectedPay.append(",").append(selectedPayMethods.get(i));
                    }
                }
            }
            min = minMoney;
            payMethods = selectedPay.toString();
            if (init) {
                model.getData(true, min, payMethods);
            }
        }
    }

}
