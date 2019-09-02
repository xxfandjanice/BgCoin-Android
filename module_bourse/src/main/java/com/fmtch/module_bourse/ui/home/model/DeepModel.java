package com.fmtch.module_bourse.ui.home.model;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.base.utils.NumberUtils;
import com.fmtch.module_bourse.bean.DeepBean;
import com.fmtch.module_bourse.bean.DeepTransformBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.fragment.DeepFragment;
import com.fmtch.module_bourse.websocket.BaseMessage;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
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

/**
 * Created by wtc on 2019/5/22
 */
public class DeepModel {
    private DeepFragment view;
    private List<BigDecimal> buyOrderList;
    private List<BigDecimal> sellOrderList;
    private OnMessageCallBack onMessageCallBack;
    private SubscribeCallBack subscribeCallBack;
    private WebSocketConnectStateListener socketConnectListener;

    public DeepBean data;

    public DeepModel(DeepFragment view) {
        this.view = view;
        buyOrderList = new ArrayList<>();
        sellOrderList = new ArrayList<>();
        //初始化创建指定个数的DeepTransformBean对象，避免刷新数据时不停的创建对象,造成内存抖动
        view.list.clear();
        for (int i = 0; i < view.depthDataSize; i++) {
            view.list.add(new DeepTransformBean());
        }
    }

    public void getData() {
        if (view.depthSubscribeResult) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("symbol", view.symbol);
        RetrofitManager.getInstance().create(ApiService.class).getDeepList(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<DeepBean>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<DeepBean>() {
                    @Override
                    public void onSuccess(DeepBean data) {
                        view.subscribe(view.depthTopic);
                        DeepModel.this.data = data;
                        updateUI();
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
                        view.depthSubscribeResult = true;
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
                    if (!channel.equals(view.depthTopic)) {
                        return;
                    }
                    BaseMessage<DeepBean> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<DeepBean>>() {
                    }.getType());
                    if (baseResponse.getChannel().equals(view.depthTopic)) {
                        data = baseResponse.getData();
                        updateUI();
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
                        view.depthSubscribeResult = false;
                    }
                }
            };
        }
        return socketConnectListener;
    }

    /**
     * 更新UI
     */
    public void updateUI() {
        if (data == null || !view.isVisible) {
            return;
        }
        List<List<BigDecimal>> asks = data.getAsks(); //卖
        List<List<BigDecimal>> bids = data.getBids(); //买
        buyOrderList.clear();
        sellOrderList.clear();

        BigDecimal maxBuyAmount = BigDecimal.ZERO;
        //获取买入数量的最大值
        if (bids.size() > 0) {
            for (List<BigDecimal> bid : bids) {
                buyOrderList.add(bid.get(1));
            }
            Collections.sort(buyOrderList);
            maxBuyAmount = buyOrderList.get(buyOrderList.size() - 1);
        }

        BigDecimal maxSellAmount = BigDecimal.ZERO;
        if (asks.size() > 0) {
            //获取卖出数量的最大值
            for (List<BigDecimal> ask : asks) {
                sellOrderList.add(ask.get(1));
            }
            Collections.sort(sellOrderList);
            maxSellAmount = sellOrderList.get(sellOrderList.size() - 1);
        }

        for (int i = 0; i < view.list.size(); i++) {
            //初始化对象属性
            DeepTransformBean transformBean = view.list.get(i);
            transformBean.setBuyAmount("");
            transformBean.setSellAmount("");
            //对象重新赋值
            if (i < bids.size()) {
                List<BigDecimal> bid = bids.get(i);
                dealBidData(maxBuyAmount,transformBean , bid);
            }
            if (i < asks.size()) {
                List<BigDecimal> ask = asks.get(i);
                dealAskData(maxSellAmount, transformBean, ask);
            }
        }

        view.adapter.setNewData(view.list);

    }

    private void dealAskData(BigDecimal maxSellAmount, DeepTransformBean d, List<BigDecimal> ask) {
        d.setSellPrice(view.market_decimal == -1 ? ask.get(0).stripTrailingZeros().toPlainString() : NumberUtils.setScale(ask.get(0), view.market_decimal));
        d.setSellAmount(view.coin_decimals == -1 ? ask.get(1).stripTrailingZeros().toPlainString() : NumberUtils.setScale(ask.get(1), view.coin_decimals));
        BigDecimal sellPercent = ask.get(1).divide(maxSellAmount, RoundingMode.HALF_DOWN);
        int sellPercentInt = (sellPercent.multiply(new BigDecimal(100)).intValue());
        d.setSellPercent(sellPercentInt);
    }

    private void dealBidData(BigDecimal maxBuyAmount, DeepTransformBean d, List<BigDecimal> bid) {
        d.setBuyPrice(view.market_decimal == -1 ? bid.get(0).stripTrailingZeros().toPlainString() : NumberUtils.setScale(bid.get(0), view.market_decimal));
        d.setBuyAmount(view.coin_decimals == -1 ? bid.get(1).stripTrailingZeros().toPlainString() : NumberUtils.setScale(bid.get(1), view.coin_decimals));
        BigDecimal buyPercent = bid.get(1).divide(maxBuyAmount, RoundingMode.HALF_DOWN);
        int buyPercentInt = (buyPercent.multiply(new BigDecimal(100)).intValue());
        d.setBuyPercent(100 - buyPercentInt);
    }
}
