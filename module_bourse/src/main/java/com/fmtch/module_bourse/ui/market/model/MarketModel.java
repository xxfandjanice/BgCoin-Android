package com.fmtch.module_bourse.ui.market.model;

import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.bean.DealOkBean;
import com.fmtch.module_bourse.bean.MarketBean;
import com.fmtch.module_bourse.event.MarketEvent;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.listener.DataChangeListener;
import com.fmtch.module_bourse.listener.DataListener;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.fragment.DealOkFragment;
import com.fmtch.module_bourse.ui.market.fragment.MarketFragment;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.websocket.BaseMessage;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/20
 */
public class MarketModel {

    private MarketFragment view;
    private OnMessageCallBack onMessageCallBack;
    private SubscribeCallBack subscribeCallBack;
    private WebSocketConnectStateListener socketConnectListener;
    private MarketBean data;

    public MarketModel(MarketFragment view) {
        this.view = view;
    }


    public void getData() {
        if (view.tickerSubscribeResult) {
            return;
        }
        RetrofitManager.getInstance().create(ApiService.class)
                .getMarkets("")
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<MarketBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<MarketBean>>() {
                    @Override
                    public void onSuccess(List<MarketBean> data) {
                        view.subscribe(view.tickerTopic);
                        for (DataListener dataListener : MarketFragment.dataListeners) {
                            dataListener.onSuccess(data);
                        }
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        for (DataListener dataListener : MarketFragment.dataListeners) {
                            dataListener.onFail(response);
                        }
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        for (DataListener dataListener : MarketFragment.dataListeners) {
                            dataListener.onException(error);
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
                    if (view.tickerTopic.equals(topic)) {
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
                    if (!channel.equals(view.tickerTopic)) {
                        return;
                    }
                    BaseMessage<MarketBean> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<MarketBean>>() {
                    }.getType());
                    data = baseResponse.getData();
                    dispatchData();
                }
            };
        }
        return onMessageCallBack;
    }

    public void dispatchData() {
        if (data == null || !view.isVisible) {
            return;
        }
        for (DataChangeListener dataChangeListener : MarketFragment.dataChangeListeners) {
            dataChangeListener.onDataChange(data);
        }
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
                    }
                }
            };
        }
        return socketConnectListener;
    }
}
