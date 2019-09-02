package com.fmtch.module_bourse.ui.trade.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.pojo.SymbolBean;
import com.fmtch.base.router.RouterMap;
import com.fmtch.base.utils.InputPwdDialogUtils;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.base.widget.dialog.CommonDialog;
import com.fmtch.base.widget.dialog.InputPayPwdDialog;
import com.fmtch.module_bourse.R;
import com.fmtch.module_bourse.bean.AccountBean;
import com.fmtch.module_bourse.bean.DeepBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.bean.MyPendOrderBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.trade.fragment.Coin_coinFragment;
import com.fmtch.module_bourse.utils.SizeUtils;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.fmtch.module_bourse.websocket.BaseMessage;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.TopicType;
import com.fmtch.module_bourse.widget.popup.DealPopup;
import com.fmtch.module_bourse.widget.popup.XGravity;
import com.fmtch.module_bourse.widget.popup.YGravity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by wtc on 2019/5/23
 */
public class CoinCoinModel {

    private Coin_coinFragment view;
    private List<BigDecimal> buyOrderList;  //买入(根据数量排序)
    private List<BigDecimal> sellOrderList; //卖出(根据数量排序)
    private DealPopup popup;
    private CommonDialog tipDialog;
    private String buyAvailable = "";   //买入可用资产
    private String sellAvailable = "";  //卖出可用资产
    private int deepSize = 10;
    private OnMessageCallBack onMessageCallBack;
    private SubscribeCallBack subscribeCallBack;
    private WebSocketConnectStateListener socketConnectListener;
    private TextView tvPriceLast;
    private TextView tvTransformCoin;
    private List<List<BigDecimal>> asks;
    private List<List<BigDecimal>> bids;
    private List<List<BigDecimal>> asksAndbids = new ArrayList<>();
    private BigDecimal maxBuyAmount;
    private BigDecimal maxSellAmount;

    public CoinCoinModel(Coin_coinFragment view) {
        buyOrderList = new ArrayList<>();
        sellOrderList = new ArrayList<>();
        this.view = view;
        initPop();
    }

    /**
     * 根据交易对加载数据
     * 传空则默认行情接口的第一个交易对
     */
    public void getData(String symbol) {
        if (!TextUtils.isEmpty(symbol)) {
            getDataBySymbol(symbol);
            return;
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .getMarketCoin("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MarketBean>>() {
                    @Override
                    public void onSuccess(List<MarketBean> data) {
                        if (data != null) {
                            for (MarketBean bean : data) {
                                if (bean.getMarket_name().equals("USDT")) {
                                    initSymbol(bean);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    /**
     * 初始化交易对相关
     *
     * @param bean
     */
    public void initSymbol(MarketBean bean) {
        view.marketBean = bean;
        view.symbol = bean.getSymbol();
        view.symbolBean = Realm.getDefaultInstance().where(SymbolBean.class).equalTo("symbol", view.symbol).findFirst();
        if (view.symbolBean != null) {
            view.coinName = view.symbolBean.getCoin_name();
            view.marketName = view.symbolBean.getMarket_name();
        }
        //更新与币种名称、市场名称有关的显示
        updateViewWithSymbol(view.symbol);
        //更新订阅主题
        updateTopic(view.symbol);
        //加载指定交易对的数据
        getDataBySymbol(view.symbol);
    }

    /***
     * 更新与币种名称、市场名称有关的显示
     */
    private void updateViewWithSymbol(String symbol) {

        view.tvCoin.setText(symbol);
        view.etLimitPrice.setText(NumberUtils.stripMoneyZeros(view.marketBean.getClose()));
        if (view.orderType == Coin_coinFragment.MARKET_PRICE_ORDER && view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
            view.etAmount.setHint((String.format(view.getString(R.string.money_coin), view.marketName)));
        } else {
            view.etAmount.setHint((String.format(view.getString(R.string.amount_coin), view.coinName)));
        }
        view.tvAmount.setText(String.format(view.getString(R.string.num_bracket), view.coinName));
        view.tvPrice.setText(String.format(view.getString(R.string.price_bracket), view.marketName));

        setLoginBuySellBtnType(view.coinName);
    }

    /**
     * 更新Socket订阅的主题
     *
     * @param symbol
     */
    public void updateTopic(String symbol) {
        //先解除订阅旧主题
        view.unSubscribes();
        view.depthSubscribeResult = false;
        view.tickerSubscribeResult = false;
        view.topics.clear();
        //行情主题
        view.tickerTopic = TopicType.TICKER_ASSIGN + symbol;
        view.topics.add(view.tickerTopic);
        //深度主题
        view.depthTopic = String.format(TopicType.DEPTH, symbol, deepSize);
        view.topics.add(view.depthTopic);
    }

    /**
     * 加载指定交易对的数据
     */
    private void getDataBySymbol(String symbol) {
        //更新该交易对的最新价、涨跌幅、折合人民币/美元的值
        getMarket(symbol);
        //获取深度数据
        getDeep(symbol);
        //获取挂单数据
        getPendOrders(symbol);
        //获取币币账户总资产,换算可买数量
        getCoinCoinTotal();
    }


    /**
     * 行情数据
     *
     * @param symbol
     */
    private void getMarket(String symbol) {
        if (view.tickerSubscribeResult) {
            return;
        }
        if (TextUtils.isEmpty(symbol)) {
            return;
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .getMarkets(symbol)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<MarketBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MarketBean>>() {
                    @Override
                    public void onSuccess(List<MarketBean> data) {
                        if (data != null) {
                            view.subscribe(view.tickerTopic);
                            view.marketBean = data.get(0);
                            updateMarketUI();
                        }
                    }
                });
    }


    /**
     * 深度数据
     *
     * @param symbol
     */
    private void getDeep(String symbol) {
        if (view.depthSubscribeResult) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("symbol", symbol);
        map.put("size", deepSize + "");
        RetrofitManager.getInstance().create(ApiService.class).getDeepList(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<DeepBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<DeepBean>() {
                    @Override
                    public void onSuccess(DeepBean data) {
                        view.subscribe(view.depthTopic);
                        view.deepData = data;
                        updateDeepUI(true);
                    }
                });

    }


    /**
     * 挂单数据
     */
    public void getPendOrders(String symbol) {
        if (!view.isLogin) {
            //未登录/退出登录
            if (!isExistFootView(view.emptyView)) {
                view.adapter.getData().clear();
                view.adapter.removeAllFooterView();
                view.adapter.addFooterView(view.emptyView);
            }
            view.tvCancelOrder.setVisibility(View.GONE);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("status", "0");
        map.put("page", "1");
        map.put("per_page", "2");
        map.put("symbol", symbol);
        RetrofitManager.getInstance().create(ApiService.class)
                .getMyPendOrders(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<MyPendOrderBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MyPendOrderBean>>() {
                    @Override
                    public void onSuccess(final List<MyPendOrderBean> data) {
                        if (data == null) {
                            return;
                        }
                        view.tvCancelOrder.setVisibility(data.size() <= 0 ? View.GONE : View.VISIBLE);
                        view.adapter.getData().clear();
                        if (data.size() == 0) {
                            view.adapter.notifyDataSetChanged();
                            if (!isExistFootView(view.emptyView)) {
                                view.adapter.removeAllFooterView();
                                view.adapter.addFooterView(view.emptyView);
                            }
                        } else {
                            view.adapter.addData(data);
                            if (!isExistFootView(view.seeAllView)) {
                                view.adapter.removeAllFooterView();
                                view.adapter.addFooterView(view.seeAllView);
                            }
                        }
                    }
                });
    }


    /**
     * 获取币币账户总资产,换算可买数量或者价格
     * 买入用market_id  卖出用coin_id
     */
    private void getCoinCoinTotal() {
        if (!view.isLogin) {
            view.tvAvailableMoney.setText(String.format(view.getString(R.string.available_money), "0.00"));
            return;
        }
        int id = -1;
        if (view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
            //买入
            id = view.symbolBean.getMarket_id();
        } else if (view.tradeType == Coin_coinFragment.TRADE_SELL_OUT) {
            //卖出
            id = view.symbolBean.getCoin_id();
        }
        if (id <= 0) {
            return;
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .getCoinCoinData(String.valueOf(id))
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<AccountBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AccountBean>() {
                    @Override
                    public void onSuccess(AccountBean data) {
                        if (data != null) {
                            if (view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
                                //买入
                                buyAvailable = data.getAvailable();
                                view.tvAvailableMoney.setText(String.format("%s %s", view.getString(R.string.available_money, buyAvailable), view.marketName));
                            } else if (view.tradeType == Coin_coinFragment.TRADE_SELL_OUT) {
                                //卖出
                                sellAvailable = data.getAvailable();
                                view.tvAvailableMoney.setText(String.format("%s %s", view.getString(R.string.available_money, sellAvailable), view.coinName));
                            }
                        }
                    }
                });

    }


    /**
     * 订阅主题的回调
     *
     * @param topics
     * @return
     */
    public SubscribeCallBack getSubscribeCallBack(final List<String> topics) {
        if (subscribeCallBack == null) {
            subscribeCallBack = new SubscribeCallBack() {
                @Override
                public void callBack(String topic, String action, boolean result) {
                    if (view.depthTopic.equals(topic)) {
                        //深度主题订阅成功
                        view.depthSubscribeResult = true;
                    } else if (view.tickerTopic.equals(topic)) {
                        //行情主题订阅成功
                        view.tickerSubscribeResult = true;
                    }
                }
            };
        }
        return subscribeCallBack;
    }

    /**
     * 订阅主题的消息
     *
     * @param topics
     * @return
     */
    public OnMessageCallBack getOnMessageCallBack(final List<String> topics) {
        if (onMessageCallBack == null) {
            onMessageCallBack = new OnMessageCallBack() {

                @Override
                public void onMessage(String message) {
                    Gson gson = new GsonBuilder().create();
                    BaseMessage baseMessage = gson.fromJson(message, BaseMessage.class);
                    String channel = baseMessage.getChannel();
                    if (view.depthTopic.equals(channel)) {
                        //深度主题消息
                        view.depthSubscribeResult = true;
                        BaseMessage<DeepBean> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<DeepBean>>() {
                        }.getType());
                        view.deepData = baseResponse.getData();
                        updateDeepUI(true);
                    } else if (view.tickerTopic.equals(channel)) {
                        //行情主题订阅消息
                        view.tickerSubscribeResult = true;
                        BaseMessage<MarketBean> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<MarketBean>>() {
                        }.getType());
                        MarketBean data = baseResponse.getData();
                        view.marketBean.setOpen(data.getOpen());
                        view.marketBean.setClose(data.getClose());
                        view.marketBean.setHigh(data.getHigh());
                        view.marketBean.setLow(data.getLow());
                        view.marketBean.setNumber(data.getNumber());
                        view.marketBean.setTotal(data.getTotal());
                        updateMarketUI();
                    }

                }
            };
        }
        return onMessageCallBack;
    }

    /**
     * socket连接断开
     *
     * @return
     */
    public WebSocketConnectStateListener getSocketConnectListener() {
        if (socketConnectListener == null) {
            socketConnectListener = new WebSocketConnectStateListener() {
                @Override
                public void OnSocketConnectState(boolean isConnect) {
                    if (!isConnect) {
                        view.tickerSubscribeResult = false;
                        view.depthSubscribeResult = false;
                    }
                }
            };
        }
        return socketConnectListener;
    }

    /**
     * 设置登录按钮的文字及样式
     * 登录注册 0  买入 1   卖出 2
     */
    public void setLoginBuySellBtnType(@Nullable String coinName) {
        if (coinName == null) {
            coinName = "";
        }
        if (!view.isLogin) {
            //登录注册
            view.tvBtnLoginBuySell.setBackgroundResource(R.drawable.shape_login_btn_blue);
            view.tvBtnLoginBuySell.setText(R.string.login_register);
            return;
        }
        if (view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
            //买入
            view.tvBtnLoginBuySell.setBackgroundResource(R.drawable.shape_btn_buy_in);
            view.tvBtnLoginBuySell.setText(String.format(view.getString(R.string.buy_in_coin), coinName));
        } else if (view.tradeType == Coin_coinFragment.TRADE_SELL_OUT) {
            //卖出
            view.tvBtnLoginBuySell.setBackgroundResource(R.drawable.shape_btn_sell_out);
            view.tvBtnLoginBuySell.setText(String.format(view.getString(R.string.sell_out_coin), coinName));
        }
    }

    /**
     * 初始化资产百分比样式
     */
    public void resetPercentNumberView() {
        if (!TextUtils.isEmpty(view.percentNumberView.getSelectedPercentNumber())) {
            view.percentNumberView.resetPercentNumberView();
        }
    }

    /**
     * 更新该交易对的最新价、涨跌幅、折合人民币/美元的值
     */
    public void updateMarketUI() {
        if (!view.isVisible) {
            return;
        }
        if (tvPriceLast != null) {
            tvPriceLast.setText(NumberUtils.stripMoneyZeros(view.marketBean.getClose()));
        }
        //计算汇率
        if (tvTransformCoin != null) {
            tvTransformCoin.setText(NumberUtils.transform2CnyOrUsd(view.marketName, view.marketBean.getClose()));
        }
        BigDecimal rate = NumberUtils.getRate(view.marketBean.getClose(), view.marketBean.getOpen());
        view.tvPercent.setText(NumberUtils.bigDecimal2Percent(rate));
        int i = rate.compareTo(BigDecimal.ZERO);
        if (i == 0) {
            view.tvPercent.setText(view.getString(R.string.zero));
        } else if (i > 0) {
            view.tvPercent.setBackgroundResource(R.drawable.shape_rec_green);
            if (tvPriceLast != null) {
                tvPriceLast.setTextColor(view.getResources().getColor(R.color.cl_03c087));
            }
        } else {
            view.tvPercent.setBackgroundResource(R.drawable.shape_rec_red);
            if (tvPriceLast != null) {
                tvPriceLast.setTextColor(view.getResources().getColor(R.color.cl_f55758));
            }
        }
    }


    /**
     * 刷新深度UI
     *
     * @param isNeedReverseAsk 是否需要将卖的数据反转
     */
    public void updateDeepUI(boolean isNeedReverseAsk) {
        if (view.deepData == null || !view.isVisible) {
            return;
        }
        //处理深度数据
        dealDepthData(view.deepData, isNeedReverseAsk);
        //将深度添加至容器
        view.llContainer.removeAllViews();
        asksAndbids.clear();
        int depthViewHeight = SizeUtils.dp2px(21);
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal maxAmount = BigDecimal.ZERO;
        for (int i = 0; i < deepSize; i++) {
            View depthView = LayoutInflater.from(view.getActivity()).inflate(R.layout.layout_trade_buy, null);
            final TextView tvPrice = depthView.findViewById(R.id.tv_buy_price);
            TextView tvAmount = depthView.findViewById(R.id.tv_buy_amount);
            ProgressBar pb = depthView.findViewById(R.id.pb_buy);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, depthViewHeight);
            depthView.setLayoutParams(layoutParams);

            List<BigDecimal> bid;
            List<BigDecimal> ask;
            if (view.rightDepthType == Coin_coinFragment.DEPTH_ALL) {
                //全部(买、卖都展示,各5条)
                if (i < deepSize / 2) {
                    ask = asks.get(deepSize / 2 + i);
                    asksAndbids.add(ask);
                    tvPrice.setTextColor(view.getResources().getColor(R.color.cl_f55758));
                    pb.setProgressDrawable(view.getResources().getDrawable(R.drawable.shape_trade_sell_progress));
                    price = ask.get(0);
                    amount = ask.get(1);
                    maxAmount = maxSellAmount;
                } else {
                    bid = bids.get(i - deepSize / 2);
                    asksAndbids.add(bid);
                    tvPrice.setTextColor(view.getResources().getColor(R.color.cl_03c087));
                    pb.setProgressDrawable(view.getResources().getDrawable(R.drawable.shape_trade_buy_progress));
                    price = bid.get(0);
                    amount = bid.get(1);
                    maxAmount = maxBuyAmount;
                }

            } else if (view.rightDepthType == Coin_coinFragment.DEPTH_BUY_IN) {
                //只展示买 10条
                bid = bids.get(i);
                asksAndbids.add(bid);
                tvPrice.setTextColor(view.getResources().getColor(R.color.cl_03c087));
                pb.setProgressDrawable(view.getResources().getDrawable(R.drawable.shape_trade_buy_progress));
                price = bid.get(0);
                amount = bid.get(1);
                maxAmount = maxBuyAmount;
            } else if (view.rightDepthType == Coin_coinFragment.DEPTH_SELL_OUT) {
                //只展示卖 10条
                ask = asks.get(i);
                asksAndbids.add(ask);
                tvPrice.setTextColor(view.getResources().getColor(R.color.cl_f55758));
                pb.setProgressDrawable(view.getResources().getDrawable(R.drawable.shape_trade_sell_progress));
                price = ask.get(0);
                amount = ask.get(1);
                maxAmount = maxSellAmount;
            }

            tvPrice.setText(price.equals(new BigDecimal(-1)) ? "" : NumberUtils.setScale(price, view.symbolBean.getPrice_decimals()));
            tvAmount.setText(amount.equals(new BigDecimal(-1)) ? "" : NumberUtils.setScale(amount, view.symbolBean.getNumber_decimals()));
            BigDecimal percent = amount.divide(maxAmount, RoundingMode.HALF_DOWN);
            int percentInt = (price.equals(new BigDecimal(-1)) ? 0 : percent.setScale(2, RoundingMode.HALF_DOWN).multiply(new BigDecimal(100)).intValue());
            pb.setProgress(100 - percentInt);
            //点击事件
            final int finalI = i;
            depthView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.etLimitPrice.setText(NumberUtils.stripMoneyZeros(asksAndbids.get(finalI).get(0).toPlainString()));
                }
            });
            view.llContainer.addView(depthView);
        }

        //最新价的View
        View marketView = LayoutInflater.from(view.getActivity()).inflate(R.layout.layout_depth_market, null);
        tvPriceLast = marketView.findViewById(R.id.tv_price_last);
        tvTransformCoin = marketView.findViewById(R.id.tv_transform_coin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, view.llContainer.getHeight() - depthViewHeight * 10);
        marketView.setLayoutParams(params);
        if (view.rightDepthType == Coin_coinFragment.DEPTH_ALL) {
            view.llContainer.addView(marketView, deepSize / 2);
        } else if (view.rightDepthType == Coin_coinFragment.DEPTH_BUY_IN) {
            view.llContainer.addView(marketView, 0);
        } else if (view.rightDepthType == Coin_coinFragment.DEPTH_SELL_OUT) {
            view.llContainer.addView(marketView, deepSize);
        }
        //更新最新价
        updateMarketUI();
    }


    /**
     * 对深度的数据进行处理
     *
     * @param data
     * @param isNeedReverseAsk
     */
    private void dealDepthData(DeepBean data, boolean isNeedReverseAsk) {
        //卖
        asks = data.getAsks();
        if (asks.size() > deepSize) {
            asks = asks.subList(0, deepSize);
        } else {
            int span = deepSize - asks.size();
            for (int i = 0; i < span; i++) {
                ArrayList<BigDecimal> list = new ArrayList<>();
                list.add(new BigDecimal(-1));
                list.add(new BigDecimal(-1));
                asks.add(list);
            }
        }
        //价格从大到小
        if (isNeedReverseAsk)
            Collections.reverse(asks);

        //买
        bids = data.getBids();
        if (bids.size() > deepSize) {
            bids = bids.subList(0, deepSize);
        } else {
            int span = deepSize - bids.size();
            for (int i = 0; i < span; i++) {
                ArrayList<BigDecimal> list = new ArrayList<>();
                list.add(new BigDecimal(-1));
                list.add(new BigDecimal(-1));
                bids.add(list);
            }
        }
        //获取买入数量的最大值
        maxBuyAmount = BigDecimal.ZERO;
        if (bids.size() > 0) {
            buyOrderList.clear();
            for (List<BigDecimal> bid : bids) {
                buyOrderList.add(bid.get(1));
            }
            Collections.sort(buyOrderList);
            maxBuyAmount = buyOrderList.get(buyOrderList.size() - 1);
        }
        //获取卖出数量的最大值
        maxSellAmount = BigDecimal.ZERO;
        if (asks.size() > 0) {
            sellOrderList.clear();
            for (List<BigDecimal> ask : asks) {
                sellOrderList.add(ask.get(1));
            }
            Collections.sort(sellOrderList);
            maxSellAmount = sellOrderList.get(sellOrderList.size() - 1);
        }
    }


    /**
     * 是否存在此脚布局
     *
     * @param footView
     * @return
     */
    private boolean isExistFootView(View footView) {
        LinearLayout footerLayout = view.adapter.getFooterLayout();
        if (footerLayout == null) {
            return false;
        }
        int childCount = footerLayout.getChildCount();
        if (childCount == 0) {
            return false;
        }
        for (int i = 0; i < childCount; i++) {
            View child = view.adapter.getFooterLayout().getChildAt(i);
            if (child == footView) {
                return true;
            }
        }
        return false;
    }

    /**
     * 撤销挂单的弹窗提示
     *
     * @param position
     */
    public void showCancelOrderDialog(final int position) {
        CommonDialog dialog = new CommonDialog(view.getActivity());
        dialog.showMsg(position <= 1 ? view.getString(R.string.dialog_tip_cancel_single_order) : view.getString(R.string.dialog_tip_cancel_all_order));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
            @Override
            public void onConfirmClick() {
                if (position > 1) {
                    cancelAllOrder();
                } else {
                    cancelOrder(position);
                }
            }
        });
    }


    /**
     * 撤销单个订单
     */
    private void cancelOrder(final int position) {
        MyPendOrderBean orderBean = view.list.get(position);
        RetrofitManager.getInstance().create(ApiService.class)
                .cancelSingleOrder(orderBean.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showShortToast(R.string.cancel_order_success);
                    }
                });
    }

    /**
     * 撤销全部订单
     */
    private void cancelAllOrder() {
        RetrofitManager.getInstance().create(ApiService.class)
                .cancelAllOrder()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        ToastUtils.showShortToast(R.string.cancel_all_order_success);
                    }
                });

    }

    /**
     * 创建订单的数据校验及弹窗提示
     */
    public void showCreateOrderDialog(final String price, final String amount) {
        if (tipDialog == null) {
            tipDialog = new CommonDialog(view.getActivity());
            tipDialog.setCanceledOnTouchOutside(false);
        }
        //数量为空
        if (TextUtils.isEmpty(amount)) {
            if (view.orderType == Coin_coinFragment.MARKET_PRICE_ORDER && view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
                tipDialog.showMsg(view.getString(R.string.input_money), true);
            } else {
                tipDialog.showMsg(view.getString(R.string.input_amount), true);
            }
            tipDialog.show();
            return;
        }
        //买入时市价单最小金额必须大于最小总价
        if (view.orderType == Coin_coinFragment.MARKET_PRICE_ORDER && view.tradeType == Coin_coinFragment.TRADE_BUY_IN && new BigDecimal(amount).compareTo(new BigDecimal(view.symbolBean.getTotal_min())) < 0) {
            tipDialog.showMsg(String.format(view.getString(R.string.min_money), NumberUtils.stripMoneyZeros(view.symbolBean.getTotal_min())), true);
            tipDialog.show();
            return;
        } else if (new BigDecimal(amount).compareTo(new BigDecimal(view.symbolBean.getNumber_min())) < 0) {
            //数量不能小于最小数量
            tipDialog.showMsg(String.format(view.getString(R.string.min_amount), NumberUtils.stripMoneyZeros(view.symbolBean.getNumber_min())), true);
            tipDialog.show();
            return;
        }
        //限价单时价格必填
        if (view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER) {
            if (TextUtils.isEmpty(price)) {
                tipDialog.showMsg(view.getString(R.string.input_limit_price), true);
                tipDialog.show();
                return;
            }
            //不能小于最小总价
            BigDecimal p = new BigDecimal(price);
            BigDecimal a = new BigDecimal(amount);
            BigDecimal minTotal = new BigDecimal(view.symbolBean.getTotal_min());

            if (p.multiply(a).compareTo(minTotal) < 0) {
                tipDialog.showMsg(String.format(view.getString(R.string.min_total), NumberUtils.stripMoneyZeros(String.valueOf(minTotal))), true);
                tipDialog.show();
                return;
            }
        }
        //不再提醒选项
        boolean noRemind = (boolean) SpUtils.get(KeyConstant.KEY_NO_REMIND_CREATE_ORDER, false);
        if (noRemind) {
            showInputPayPwdDialog(price, amount, false);
            return;
        }

        CommonDialog dialog = new CommonDialog(view.getActivity());
        dialog.setCanceledOnTouchOutside(false);
        final View inflate = LayoutInflater.from(this.view.getActivity()).inflate(R.layout.layout_create_order_sure_dialog, null);
        dialog.addContentView(inflate);
        TextView tvTitle = inflate.findViewById(R.id.tv_title);
        TextView tvPrice = inflate.findViewById(R.id.tv_price);
        TextView tvAmount = inflate.findViewById(R.id.tv_amount);
        final TextView tvNoRemind = inflate.findViewById(R.id.tv_no_remind);

        String orderType;
        String buyOrSell;
        orderType = view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER ? view.getString(R.string.limit_price_order) : view.getString(R.string.market_price_order);
        buyOrSell = view.tradeType == Coin_coinFragment.LIMIT_PRICE_ORDER ? view.getString(R.string.buy_in) : view.getString(R.string.sell_out);
        tvTitle.setText(String.format("%s%s", orderType, buyOrSell));
        tvPrice.setText(String.format(view.getString(R.string.create_order_price), "   " + (view.orderType == 2 ? view.getString(R.string.market_order_tip) : price), view.marketName));
        if (view.orderType == Coin_coinFragment.MARKET_PRICE_ORDER && view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
            tvAmount.setText(String.format(view.getString(R.string.create_order_money), "   " + amount, view.marketName));
        } else {
            tvAmount.setText(String.format(view.getString(R.string.create_order_amount), "   " + amount, view.coinName));
        }

        //下次不再提醒
        tvNoRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean noRemind = (boolean) SpUtils.get(KeyConstant.KEY_NO_REMIND_CREATE_ORDER, false);
                Drawable drawable;
                if (!noRemind) {
                    drawable = view.getResources().getDrawable(R.mipmap.icon_selected);
                } else {
                    drawable = view.getResources().getDrawable(R.mipmap.icon_unselected);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvNoRemind.setCompoundDrawables(drawable, null, null, null);
                SpUtils.put(KeyConstant.KEY_NO_REMIND_CREATE_ORDER, !noRemind);
            }
        });
        //确认下单
        dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
            @Override
            public void onConfirmClick() {
                showInputPayPwdDialog(price, amount, false);
            }
        });
        dialog.show();
    }

    /**
     * 输入资金密码的弹窗
     *
     * @param price
     * @param amount
     */
    private void showInputPayPwdDialog(final String price, final String amount, boolean isServerNeedPwd) {
        int show = InputPwdDialogUtils.showPwdDialog();
        if (show == InputPwdDialogUtils.STATUS_UN_SET_PAY_PWD) {
            CommonDialog dialog = new CommonDialog(view.getActivity());
            dialog.showMsg(view.getString(R.string.no_set_trade_pwd), false);
            dialog.setBtnConfirmText(view.getString(R.string.go_set));
            dialog.show();
            dialog.setOnConfirmClickListener(new CommonDialog.OnConfirmButtonClickListener() {
                @Override
                public void onConfirmClick() {
                    ARouter.getInstance().build(RouterMap.SET_ASSETS_PWD).navigation();
                }
            });
        } else if (show == InputPwdDialogUtils.STATUS_UN_SHOW && !isServerNeedPwd) {
            createOrder(price, amount, "");
        } else if (show == InputPwdDialogUtils.STATUS_SHOW) {
            new InputPayPwdDialog(view.getActivity())
                    .setOnConfirmSuccessListener(new InputPayPwdDialog.OnConfirmSuccessListener() {
                        @Override
                        public void onConfirmSuccess(String pay_pwd) {
                            createOrder(price, amount, pay_pwd);
                        }
                    })
                    .builder()
                    .show();
        }
    }

    /**
     * 调用接口创建订单
     */
    private void createOrder(final String price, final String amount, final String pay_pwd) {
        Map<String, String> map = new HashMap<>();
        map.put("symbol", view.symbol);
        map.put("side", view.tradeType == Coin_coinFragment.TRADE_BUY_IN ? "BUY" : "SELL");  //BUY-买入    SELL-卖出
        map.put("order_type", view.orderType == Coin_coinFragment.TRADE_BUY_IN ? "LIMIT" : "MARKET");  //LIMIT- 限价单    MARKET-市价单
        if (view.orderType == Coin_coinFragment.TRADE_BUY_IN) {
            map.put("price", price);  //订单类型为限价单时价格必填
        }
        map.put("number", amount);  //数量
        if (!TextUtils.isEmpty(pay_pwd)) {
            map.put("pay_password", pay_pwd);  //交易密码验证类型: 0:每次都验证；1:不验证；2:每两小时验证一次
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .createOrder(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<String>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onSuccess(String data) {
                        if (!TextUtils.isEmpty(pay_pwd)) {
                            //保存支付密码校验成功的时间戳
                            SpUtils.put(KeyConstant.KEY_PAY_PWD_SUCCESS_TIME, System.currentTimeMillis());
                        }
                        ToastUtils.showShortToast(R.string.creat_order_success);
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        //需要输入资金密码
                        if (response.getCode() == 60003) {
                            showInputPayPwdDialog(price, amount, true);
                        } else {
                            CommonDialog dialog = new CommonDialog(view.getActivity());
                            dialog.showMsg(response.getMessage(), true);
                            dialog.show();
                        }
                    }
                });

    }


    /**
     * 根据资产的百分比计算可买/卖的数量
     */
    public void amountByPercentNumber(String percent) {
        if (!view.isLogin) {
            ToastUtils.showShortToast(R.string.please_login);
            resetPercentNumberView();
            return;
        }
        BigDecimal price = BigDecimal.ZERO;
        //限价单价格校验
        if (view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER) {
            String inputPrice = view.etLimitPrice.getText().toString();
            if (TextUtils.isEmpty(inputPrice)) {
                ToastUtils.showShortToast(R.string.please_input_price);
                resetPercentNumberView();
                return;
            }
            price = new BigDecimal(inputPrice);
            if (price.compareTo(BigDecimal.ZERO) == 0) {
                ToastUtils.showShortToast(R.string.please_input_price_more_zero);
                resetPercentNumberView();
                return;
            }
        }
        int scale;
        String amount = "";
        BigDecimal available;
        //保留小数位数
        if (view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
            if (TextUtils.isEmpty(buyAvailable)) {
                ToastUtils.showShortToast("可用余额未获取,请稍后再试");
                resetPercentNumberView();
                return;
            }
            //买入
            scale = view.orderType == Coin_coinFragment.MARKET_PRICE_ORDER ? view.symbolBean.getPrice_decimals() : view.symbolBean.getNumber_decimals();
            available = new BigDecimal(buyAvailable).multiply(new BigDecimal(percent));
            if (view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER) {
                amount = available.divide(price, RoundingMode.DOWN).setScale(scale, RoundingMode.DOWN).toPlainString();
            } else {
                amount = available.setScale(scale, RoundingMode.DOWN).toPlainString();
            }
        } else if (view.tradeType == Coin_coinFragment.TRADE_SELL_OUT) {
            if (TextUtils.isEmpty(sellAvailable)) {
                ToastUtils.showShortToast("可用余额未获取,请稍后再试");
                resetPercentNumberView();
                return;
            }
            //卖出
            scale = view.symbolBean.getNumber_decimals();
            amount = new BigDecimal(sellAvailable).multiply(new BigDecimal(percent)).setScale(scale, RoundingMode.DOWN).toPlainString();
        }
        view.etAmount.setText(amount);
    }


    public void showPoP(View targetView) {
        popup.showAsDropDown(targetView, YGravity.BELOW, XGravity.CENTER);
    }

    private void initPop() {
        popup = DealPopup.create(view.getActivity())
                .setMyOnItemClickListener(new DealPopup.OnItemClickListener() {
                    @Override
                    public void itemClick(View v, int pos) {
                        popup.updatePos(pos);
                        view.percentNumberView.resetPercentNumberView();
                        view.tvPop.setText(popup.getNowText(pos));
                        view.orderType = pos == 2 ? Coin_coinFragment.MARKET_PRICE_ORDER : Coin_coinFragment.LIMIT_PRICE_ORDER;
                        view.llInputLimitPrice.setVisibility(view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER ? View.VISIBLE : View.GONE);
                        view.tvMarketPrice.setVisibility(view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER ? View.GONE : View.VISIBLE);
                        view.tvValuation.setVisibility(view.orderType == Coin_coinFragment.LIMIT_PRICE_ORDER ? View.VISIBLE : View.INVISIBLE);
                        if (view.orderType == Coin_coinFragment.MARKET_PRICE_ORDER && view.tradeType == Coin_coinFragment.TRADE_BUY_IN) {
                            view.etAmount.setHint((String.format(view.getString(R.string.money_coin), view.marketName)));
                        } else {
                            view.etAmount.setHint((String.format(view.getString(R.string.amount_coin), view.coinName)));
                        }
                    }
                })
                .apply();
    }

    /**
     * 价格加减
     *
     * @param add
     */
    public void addPrice(boolean add) {
        String price_step = view.symbolBean.getPrice_step();
        String price = view.etLimitPrice.getText().toString();
        if (TextUtils.isEmpty(price)) {
            price = "0.00";
        }
        BigDecimal step = new BigDecimal(price_step);
        BigDecimal prices = new BigDecimal(price);
        String s;
        if (add) {
            s = step.add(prices).stripTrailingZeros().toPlainString();
        } else {
            if (prices.compareTo(step) < 0) {
                return;
            } else {
                s = prices.subtract(step).stripTrailingZeros().toPlainString();
            }
        }
        view.etLimitPrice.setText(s);
        if (view.etLimitPrice.hasFocus()) {
            view.etLimitPrice.setSelection(view.etLimitPrice.getText().length());
        }
    }

    /**
     * 数量加减
     *
     * @param add
     */
    public void addAmount(boolean add) {
        String number_step = view.symbolBean.getNumber_step();
        String amount = view.etAmount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            amount = "0.00";
        }
        BigDecimal step = new BigDecimal(number_step);
        BigDecimal amounts = new BigDecimal(amount);
        String s;
        if (add) {
            s = step.add(amounts).stripTrailingZeros().toPlainString();
        } else {
            if (amounts.compareTo(step) < 0) {
                return;
            } else {
                s = amounts.subtract(step).stripTrailingZeros().toPlainString();
            }
        }
        view.etAmount.setText(s);
        if (view.etAmount.hasFocus()) {
            view.etAmount.setSelection(view.etAmount.getText().length());
        }
    }


    /**
     * 价格或数量加减
     *
     * @param add
     * @param isPrice
     */
    public void addOrSubtract(boolean add, boolean isPrice) {

        String step = isPrice ? view.symbolBean.getPrice_step() : view.symbolBean.getNumber_step();
        String original = isPrice ? view.etLimitPrice.getText().toString() : view.etAmount.getText().toString();
        if (TextUtils.isEmpty(original)) {
            original = "0.00";
        }
        BigDecimal stepBd = new BigDecimal(step);
        BigDecimal originalBd = new BigDecimal(original);
        String result;
        if (add) {
            result = stepBd.add(originalBd).stripTrailingZeros().toPlainString();
        } else {
            if (originalBd.compareTo(stepBd) <= 0) {
                return;
            }
            result = originalBd.subtract(stepBd).stripTrailingZeros().toPlainString();
        }
        if (isPrice) {
            view.etLimitPrice.setText(result);
            view.etLimitPrice.setSelection(view.etLimitPrice.getText().length());
        } else {
            view.etAmount.setText(result);
            view.etAmount.setSelection(view.etAmount.getText().length());
        }
    }
}
