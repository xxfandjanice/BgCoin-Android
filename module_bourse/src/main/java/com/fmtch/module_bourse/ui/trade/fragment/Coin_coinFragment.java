package com.fmtch.module_bourse.ui.trade.fragment;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fmtch.base.event.EventBean;
import com.fmtch.base.event.EventType;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.PageConstant;
import com.fmtch.module_bourse.Bourse_MainActivity;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.R2;
import com.fmtch.module_bourse.adapter.CoinCoinAdapter;
import com.fmtch.module_bourse.base.LazyReFreshDataBaseFragment;
import com.fmtch.module_bourse.bean.DeepBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.bean.MyPendOrderBean;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.trade.model.CoinCoinModel;
import com.fmtch.module_bourse.utils.AppUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.widget.PercentNumberView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;

/**
 * Created by wtc on 2019/5/13
 */
public class Coin_coinFragment extends LazyReFreshDataBaseFragment {

    @BindView(R2.id.rv)
    RecyclerView rv;
    @BindView(R2.id.tv_coin)
    public TextView tvCoin;
    @BindView(R2.id.tv_percent)
    public TextView tvPercent;
    @BindView(R2.id.tv_tab_buy_in)
    public TextView tvTabBuyIn;
    @BindView(R2.id.tv_tab_sell_out)
    public TextView tvTabSellOut;
    @BindView(R2.id.tv_pop)
    public TextView tvPop;
    @BindView(R2.id.tv_market_price)
    public TextView tvMarketPrice;
    @BindView(R2.id.et_limit_price)
    public EditText etLimitPrice;
    @BindView(R2.id.ll_input_limit_price)
    public LinearLayout llInputLimitPrice;
    @BindView(R2.id.tv_valuation)
    public TextView tvValuation;
    @BindView(R2.id.et_amount)
    public EditText etAmount;
    @BindView(R2.id.tv_available_money)
    public TextView tvAvailableMoney;
    @BindView(R2.id.percent_number_view)
    public PercentNumberView percentNumberView;
    @BindView(R2.id.tv_btn_login_buy_sell)
    public TextView tvBtnLoginBuySell;
    @BindView(R2.id.tv_price)
    public TextView tvPrice;
    @BindView(R2.id.tv_amount)
    public TextView tvAmount;
    @BindView(R2.id.ll_container)
    public LinearLayout llContainer;
    @BindView(R2.id.tv_cancel_order)
    public TextView tvCancelOrder;

    public static int LIMIT_PRICE_ORDER = 1;             //限价单
    public static int MARKET_PRICE_ORDER = 2;            //市价单
    public static int TRADE_BUY_IN = 1;                  //左侧交易 买入
    public static int TRADE_SELL_OUT = 2;                //左侧交易 卖出
    public static int DEPTH_ALL = 0;                     //右边深度 全部
    public static int DEPTH_BUY_IN = 1;                  //右边深度 买
    public static int DEPTH_SELL_OUT = 2;                //右边深度 卖

    public String symbol;                                //交易对名称
    public String coinName;                              //币种名称
    public String marketName;                            //市场名称
    public boolean isLogin;                              //是否登录

    public int orderType = LIMIT_PRICE_ORDER;            //订单类型  限价单 1  市价单 2
    public int tradeType = TRADE_BUY_IN;                 //交易类型  买入 1   卖出 2
    public int rightDepthType = DEPTH_ALL;               //右边深度  全部 0   买入 1  卖出 2

    private CoinCoinModel model;
    public List<MyPendOrderBean> list;
    public BaseQuickAdapter<MyPendOrderBean, BaseViewHolder> adapter;
    public View seeAllView;
    public View emptyView;
    public MarketBean marketBean;
    public SymbolBean symbolBean;
    public DeepBean deepData;
    public String depthTopic;                           //深度主题名称
    public String tickerTopic;                          //行情主题名称
    public boolean depthSubscribeResult = false;        //深度是否订阅成功
    public boolean tickerSubscribeResult = false;       //行情是否订阅成功
    public ArrayList<String> topics = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coin_coin;
    }

    @Override
    protected void initData() {
        super.initData();
        model = new CoinCoinModel(this);
        list = new ArrayList<>();
    }

    @Override
    protected void initView() {
        super.initView();
        initFootView();
        initTab();
        initAdapter();
    }

    private void initFootView() {
        seeAllView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_see_all, null);
        emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_trade_empty_view, null);
    }

    private void initTab() {
        tvTabBuyIn.setSelected(true);
        tvTabSellOut.setSelected(false);
        isLogin = AppUtils.isLogin();
        model.setLoginBuySellBtnType("");
    }

    private void initAdapter() {
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        adapter = new CoinCoinAdapter(getActivity(), R.layout.item_trade_limit_price, list);
        //点击撤单
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                model.showCancelOrderDialog(position);
            }
        });
        rv.setAdapter(adapter);
    }

    @Override
    public void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        //加载数据
        model.getData(symbol);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        model.updateDeepUI(false);
    }

    @Override
    public void pollingData() {
        model.getData(symbol);
    }

    @Override
    public List<String> getTopics() {
        return topics;
    }

    @Override
    public SubscribeCallBack getSubscribeCallBack() {
        return model.getSubscribeCallBack(topics);
    }

    @Override
    public OnMessageCallBack getOnMessageCallBack() {
        return model.getOnMessageCallBack(topics);
    }

    @Override
    public String getSubscribeKey() {
        return this.getClass().getSimpleName();
    }

    @Override
    public WebSocketConnectStateListener getSocketConnectListener() {
        return model.getSocketConnectListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBean event) {
        if (event.getTag() == EventType.SELECT_SYMBOL) {
            //切换币种
            model.initSymbol((MarketBean) event.data);
            //指定买入还是卖出
            int buyOrSell = marketBean.getBuyOrSell();
            if (buyOrSell == TRADE_BUY_IN || buyOrSell == TRADE_SELL_OUT) {
                tradeType = buyOrSell;
                clickTabBuyOrSell(tradeType == TRADE_BUY_IN ? R.id.tv_tab_buy_in : R.id.tv_tab_sell_out);
            }
        } else if (event.getTag() == EventType.USER_LOGIN) {
            //登录成功
            isLogin = true;
            model.setLoginBuySellBtnType(coinName);
            model.getPendOrders(symbol);
        } else if (event.getTag() == EventType.USER_EXIT) {
            //退出登录
            isLogin = false;
            model.setLoginBuySellBtnType("");
            model.getPendOrders(symbol);
        }
    }

    @OnClick({R2.id.tv_coin, R2.id.tv_percent, R2.id.tv_pop, R2.id.tv_limit_decrease, R2.id.tv_limit_increase, R2.id.tv_amount_decrease, R2.id.tv_amount_increase, R2.id.tv_btn_login_buy_sell, R2.id.tv_tab_buy_in, R2.id.tv_tab_sell_out,
            R2.id.iv_buy_sell, R2.id.iv_buy, R2.id.iv_sell, R2.id.tv_cancel_order})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.tv_coin) {                        //切换币种
            ((Bourse_MainActivity) getActivity()).openDrawerLayout(Bourse_MainActivity.DRAWER_COIN_COIN_TRADE_FILTER);
        } else if (id == R.id.tv_percent) {             //涨跌幅 进入K线页面
            ARouter.getInstance().build(RouterMap.K_LINE).withString(PageConstant.SYMBOL, symbol).navigation();
        } else if (id == R.id.tv_pop) {                 //限时买入 弹窗
            model.showPoP(tvPop);
        } else if (id == R.id.tv_limit_decrease) {      //价格 减
            model.addOrSubtract(false,true);
        } else if (id == R.id.tv_limit_increase) {      //价格 加
            model.addOrSubtract(true,true);
        } else if (id == R.id.tv_amount_decrease) {     //数量 减
            model.addOrSubtract(false,false);
        } else if (id == R.id.tv_amount_increase) {     //数量 加
            model.addOrSubtract(true,false);
        } else if (id == R.id.tv_btn_login_buy_sell) {  //登录/注册/买入/卖出
            if (!isLogin) {
                ARouter.getInstance().build(RouterMap.ACCOUNT_LOGIN).navigation();
            } else {
                //创建订单
                model.showCreateOrderDialog(etLimitPrice.getText().toString(), etAmount.getText().toString());
            }
        } else if (id == R.id.tv_tab_buy_in || id == R.id.tv_tab_sell_out) {  //左边 tab 买入 tab 卖出
            clickTabBuyOrSell(id);
        } else if (id == R.id.iv_buy_sell) {            //右边  全部
            rightDepthType = DEPTH_ALL;
            model.updateDeepUI(false);
        } else if (id == R.id.iv_buy) {                 //右边  买
            rightDepthType = DEPTH_BUY_IN;
            model.updateDeepUI(false);
        } else if (id == R.id.iv_sell) {                //右边  卖
            rightDepthType = DEPTH_SELL_OUT;
            model.updateDeepUI(false);
        } else if (id == R.id.tv_cancel_order) {        //全部撤单
            model.showCancelOrderDialog(100);
        }
    }

    /**
     * 点击切换左侧买/卖
     *
     * @param id
     */
    private void clickTabBuyOrSell(int id) {
        tvAvailableMoney.setText(getString(R.string.available));
        tvTabBuyIn.setSelected(id == R.id.tv_tab_buy_in);
        tvTabSellOut.setSelected(id == R.id.tv_tab_sell_out);
        etAmount.setHint((String.format(getString(R.string.amount_coin), coinName)));
        tradeType = id == R.id.tv_tab_buy_in ? TRADE_BUY_IN : TRADE_SELL_OUT;
        if (id == R.id.tv_tab_buy_in && orderType == MARKET_PRICE_ORDER) {
            etAmount.setHint((String.format(getString(R.string.money_coin), marketName)));
        }
        model.setLoginBuySellBtnType(coinName);
        model.resetPercentNumberView();
    }

    public String getSymbol() {
        return symbol;
    }

    //估值随价格变化
    @OnTextChanged(value = R2.id.et_limit_price, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChangedPrice(Editable s) {
        String str = s.toString();
        if (!TextUtils.isEmpty(str) && str.trim().startsWith(".")) {
            s.insert(0, "0");
        }
        if (symbolBean != null) {
            s = NumberUtils.setInputScale(s, symbolBean.getPrice_decimals());
        }
        String price = NumberUtils.transform2CnyOrUsdSymbol(marketName, s.toString());
        tvValuation.setText(String.format(getString(R.string.valuation), price));
        model.resetPercentNumberView();
    }

    //控制输入的小数位
    @OnTextChanged(value = R2.id.et_amount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChangedAmount(Editable s) {
        String str = s.toString();
        if (!TextUtils.isEmpty(str) && str.trim().startsWith(".")) {
            s.insert(0, "0");
        }

        if (symbolBean == null) {
            return;
        }
        if (tvTabBuyIn.isSelected() && orderType == MARKET_PRICE_ORDER) {
            NumberUtils.setInputScale(s, symbolBean.getPrice_decimals());
        } else {
            NumberUtils.setInputScale(s, symbolBean.getNumber_decimals());
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        //查看全部
        seeAllView.findViewById(R.id.tv_see_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterMap.MY_PEND_ORDER).navigation();
            }
        });
        //资产百分比点击
        percentNumberView.setPercentNumberSelectedListener(new PercentNumberView.PercentNumberSelectedListener() {
            @Override
            public void onClickPercentNumber(PercentNumberView view, String selectedPercentNumber) {
                model.amountByPercentNumber(selectedPercentNumber);
            }
        });
    }
}
