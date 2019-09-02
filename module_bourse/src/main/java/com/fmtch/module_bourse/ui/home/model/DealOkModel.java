package com.fmtch.module_bourse.ui.home.model;

import android.text.TextUtils;

import com.fmtch.base.manager.RetrofitManager;
import com.fmtch.module_bourse.bean.DealOkBean;
import com.fmtch.module_bourse.bean.DeepBean;
import com.fmtch.module_bourse.http.ApiService;
import com.fmtch.module_bourse.http.BaseObserver;
import com.fmtch.module_bourse.http.BaseResponse;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.ui.home.fragment.DealOkFragment;
import com.fmtch.module_bourse.websocket.BaseMessage;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.schedulers.Schedulers;

/**
 * Created by wtc on 2019/5/22
 */
public class DealOkModel {
    private DealOkFragment view;
    private OnMessageCallBack onMessageCallBack;
    private SubscribeCallBack subscribeCallBack;
    private WebSocketConnectStateListener socketConnectListener;
    private List<DealOkBean> data;

    public DealOkModel(DealOkFragment view) {
        this.view = view;
    }

    public void getData() {
        if (view.tradeSubscribeResult) {
            return;
        }
        view.adapter.showLoadingPage();
        Map<String, String> map = new HashMap<>();
        map.put("symbol", view.symbol);
        RetrofitManager.getInstance().create(ApiService.class).getDealOkList(map)
                .subscribeOn(Schedulers.io())
                .compose(view.<BaseResponse<List<DealOkBean>>>bindUntilEvent(FragmentEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<List<DealOkBean>>() {
                    @Override
                    public void onSuccess(List<DealOkBean> data) {
                        view.subscribe(view.dealOkTopic);
                        DealOkModel.this.data = data;
                        updateUI();
                    }

                    @Override
                    public void onFail(BaseResponse response) {
                        super.onFail(response);
                        view.adapter.showErrorPage(TextUtils.isEmpty(response.getMessage()) ? "" : response.getMessage());
                    }

                    @Override
                    public void onException(Throwable error) {
                        super.onException(error);
                        view.adapter.showErrorPage("");
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
                    if (view.dealOkTopic.equals(topic)) {
                        view.tradeSubscribeResult = true;
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
                    if (!channel.equals(view.dealOkTopic)) {
                        return;
                    }
                    BaseMessage<List<DealOkBean>> baseResponse = gson.fromJson(message, new TypeToken<BaseMessage<List<DealOkBean>>>() {
                    }.getType());
                    data = baseResponse.getData();
                    updateUI();
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
                        view.tradeSubscribeResult = false;
                    }
                }
            };
        }
        return socketConnectListener;
    }

    public void updateUI() {
        if (data == null || !view.isVisible) {
            return;
        }
        view.list.clear();
        if (data.size() == 0) {
            view.adapter.showEmptyPage();
        } else {
            view.list.addAll(data);
            view.adapter.setNewData(view.list);
        }
    }
}
