package com.fmtch.module_bourse.websocket;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.utils.GZipUtil;
import com.fmtch.module_bourse.utils.LogUtils;
import com.fmtch.module_bourse.utils.NetworkUtils;
import com.fmtch.module_bourse.utils.ToastUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.icechao.klinelib.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

/**
 * Created by wtc on 2019/8/7
 */
public class WebSocketManager extends WebSocketListener {

    private static final String TAG = "WebSocketManager";

    private static final String SUB = "sub";
    private static final String UNSUB = "unsub";

    public static String WS = "ws://ws.etf.top";
    private WebSocket webSocket;
    private Request request;
    private OkHttpClient client;
    private boolean isReconnect;      //是否是重连行为
    private long lastHeartTimeStamp;  //上次心跳时间
    private int heartTime = 15;       //15s内没有心跳则认为socket已断开
    private boolean isConnected;      //是否连接成功

    private List<String> subTopics = new ArrayList<>();    //保存订阅的主题
    private List<String> unSubTopics = new ArrayList<>();  //保存解除订阅的主题
    private Disposable timer;
    private Map<String, SubscribeCallBack> subscribeMap = new HashMap<>();
    private Map<String, OnMessageCallBack> messageMap = new HashMap<>();
    private Map<String, WebSocketConnectStateListener> connectListenerMap = new HashMap<>();
    private SocketConnectListener connectListener;
    private Disposable reConnectDisposable;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static class SingleHolder {
        private static WebSocketManager INSTANCE = new WebSocketManager();
    }

    public static WebSocketManager getInstance() {
        return WebSocketManager.SingleHolder.INSTANCE;
    }


    public void initWebSocket(SocketConnectListener connectListener) {
        this.connectListener = connectListener;
        if (client == null) {
            request = new Request.Builder()
                    .url(WS)
                    .build();
            client = new OkHttpClient.Builder()
                    .build();
        }
        client.newWebSocket(request, this);
    }


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        this.webSocket = webSocket;
        isConnected = true;
        //断开重连成功后自动重新订阅/解除订阅主题
        if (isReconnect) {
            LogUtils.e(TAG, "WebSocket重新连接成功:"+WS);
            reSubscribes();
            reUnSubscribes();
        } else {
            LogUtils.e(TAG, "WebSocket连接成功:"+WS);
        }
        if (this.connectListener != null) {
            this.connectListener.onSuccess();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, final String text) {
        super.onMessage(webSocket, text);
        isConnected = true;
        //解析消息
        parseMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        isConnected = true;
        byte[] byteArray = bytes.toByteArray();
        String json = GZipUtil.uncompressToString(byteArray);
        //解析消息
        parseMessage(json);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        LogUtils.e(TAG, "onClosing:" + "  code-" + code + "  reason-" + reason);
        isConnected = false;
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        LogUtils.e(TAG, "onClosed:" + "  code-" + code + "  reason-" + reason);
        //尝试重新连接
        tryReConnectSocket();
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        LogUtils.e(TAG, "WebSocket断开连接");
        t.printStackTrace();
        //尝试重新连接
        tryReConnectSocket();
    }

    /**
     * 解析消息
     *
     * @param text
     */
    private void parseMessage(final String text) {
        JsonObject msg = (JsonObject) new JsonParser().parse(text);
        if (msg.has(MessageType.PING)) {
            //心跳包
//            LogUtils.d(TAG, "心跳:" + text);
            checkHeartbeat(text);
        } else if (msg.has(MessageType.SUBBED)) {
            //订阅结果
            LogUtils.e(TAG, "订阅结果:" + text);
            subscribeResult(msg);
        } else if (msg.has(MessageType.UNSUBBED)) {
            //解除订阅结果
            LogUtils.e(TAG, "解除订阅结果:" + text);
            unSubscribeResult(msg);
        } else if (msg.has(MessageType.CHANNEL) && !messageMap.isEmpty()) {
            //主题消息
//            LogUtils.d(TAG, "onMessage:" + text);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<String, OnMessageCallBack> entry : messageMap.entrySet()) {
                        if (entry.getValue() != null) {
                            entry.getValue().onMessage(text);
                        }
                    }
                }
            });
        }
    }


    /**
     * 检查心跳
     */
    private void checkHeartbeat(String msg) {
        lastHeartTimeStamp = System.currentTimeMillis();
        String pong = msg.replace("ping", "pong");
        webSocket.send(pong);
        //开启定时器,15s内没有再次收到心跳包,则认为socket已断开连接
        if (timer == null || timer.isDisposed()) {
            timer = Observable.interval(0, heartTime, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) {
                            long value = (System.currentTimeMillis() - lastHeartTimeStamp) / 1000;
                            if (value > heartTime) {
                                //15s内没收到心跳
                                timer.dispose();
                                LogUtils.e(TAG, "心跳停止");
                                //尝试重新连接
                                tryReConnectSocket();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }

    }


    /**
     * 尝试重新连接
     * 5s发起一次连接,3次还是连接不上则停止重试
     */
    private void tryReConnectSocket() {
        isConnected = false;
        if (webSocket != null) {
            webSocket.cancel();
            webSocket = null;
        }
        //通知所有订阅者连接已断开
        publishSocketDisConnected();
        if (reConnectDisposable == null || reConnectDisposable.isDisposed()) {
            reConnectDisposable = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) {
                            if (aLong > 3 || isConnected) {
                                reConnectDisposable.dispose();
                                return;
                            }
                            LogUtils.e(TAG, "正在重新连接..." + aLong);
                            if (NetworkUtils.isConnected()) {
                                isReconnect = true;
                                initWebSocket(null);
                            } else {
                                ToastUtils.showShortToast("网络连接异常,请检查您的网络");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }

    }

    /**
     * 通知所有订阅者Socket断开连接
     */
    private void publishSocketDisConnected() {
        if (connectListenerMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, WebSocketConnectStateListener> entry : connectListenerMap.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().OnSocketConnectState(false);
            }
        }
    }

    /**
     * 断开重连后自动订阅之前的主题
     */
    private void reSubscribes() {
        if (webSocket == null || subTopics == null || subTopics.size() <= 0) {
            return;
        }
        for (String topic : subTopics) {
            if (!TextUtils.isEmpty(topic)) {
                JsonObject json = new JsonObject();
                json.addProperty(SUB, topic);
                LogUtils.e(TAG, "重新发起订阅:" + json.toString());
                webSocket.send(json.toString());
            }
        }
    }

    /**
     * 断开重连后自动解除之前未解除的主题
     */
    private void reUnSubscribes() {
        if (webSocket == null || unSubTopics == null || unSubTopics.size() <= 0) {
            return;
        }
        for (String topic : unSubTopics) {
            if (!TextUtils.isEmpty(topic)) {
                JsonObject json = new JsonObject();
                json.addProperty(UNSUB, topic);
                LogUtils.e(TAG, "重新解除订阅:" + json.toString());
                webSocket.send(json.toString());
            }
        }
    }


    /**
     * 订阅多主题
     *
     * @param topics
     * @return
     */
    public void subscribes(String key, List<String> topics, SubscribeCallBack callBack, OnMessageCallBack messageCallBack, WebSocketConnectStateListener socketConnectListener) {
        if (webSocket == null || topics == null || topics.size() <= 0) {
            return;
        }
        subscribeMap.put(key, callBack);
        messageMap.put(key, messageCallBack);
        connectListenerMap.put(key, socketConnectListener);
        for (String topic : topics) {
            if (!TextUtils.isEmpty(topic)) {
                subTopics.add(topic);
                JsonObject json = new JsonObject();
                json.addProperty(SUB, topic);
                LogUtils.e(TAG, "发起订阅:" + json.toString());
                webSocket.send(json.toString());
            }
        }

    }

    /**
     * 订阅主题
     *
     * @param topic
     * @return
     */
    public void subscribe(String key, String topic, SubscribeCallBack callBack, OnMessageCallBack messageCallBack, WebSocketConnectStateListener socketConnectListener) {
        if (webSocket == null || TextUtils.isEmpty(topic)) {
            return;
        }
        subscribeMap.put(key, callBack);
        messageMap.put(key, messageCallBack);
        connectListenerMap.put(key, socketConnectListener);
        subTopics.add(topic);
        JsonObject json = new JsonObject();
        json.addProperty(SUB, topic);
        LogUtils.e(TAG, "发起订阅:" + json.toString());
        webSocket.send(json.toString());
    }


    /**
     * 解除订阅多主题
     *
     * @param topics
     * @return
     */
    public void unSubscribes(String key, List<String> topics) {
        if (webSocket == null || topics == null || topics.size() <= 0) {
            return;
        }
        subscribeMap.remove(key);
        messageMap.remove(key);
        connectListenerMap.remove(key);
        for (String topic : topics) {
            if (!TextUtils.isEmpty(topic)) {
                //订阅的主题在subTopics中出现次数大于1,表示别的地方也订阅了这个主题,无需解除订阅
                int count = Collections.frequency(subTopics, topic);
                if (count > 1) {
                    subTopics.remove(topic);
                    continue;
                }
                JsonObject json = new JsonObject();
                json.addProperty(UNSUB, topic);
                LogUtils.e(TAG, "解除订阅:" + json.toString());
                webSocket.send(json.toString());
            }
        }

    }

    /**
     * 解除订阅主题
     *
     * @param topic
     * @return
     */
    public void unSubscribe(String key, String topic) {
        if (webSocket == null || TextUtils.isEmpty(topic)) {
            return;
        }
        subscribeMap.remove(key);
        messageMap.remove(key);
        connectListenerMap.remove(key);
        //订阅的主题在subTopics中出现次数大于1,表示别的地方也订阅了这个主题,无需解除订阅
        int count = Collections.frequency(subTopics, topic);
        if (count > 1) {
            subTopics.remove(topic);
            return;
        }
        JsonObject json = new JsonObject();
        json.addProperty(UNSUB, topic);
        LogUtils.e(TAG, "解除订阅:" + json.toString());
        webSocket.send(json.toString());
    }

    /**
     * 订阅结果
     */
    private void subscribeResult(JsonObject msg) {
        if (!subscribeMap.isEmpty()) {
            String topic = msg.get(MessageType.SUBBED).getAsString();
            boolean result = msg.get("status").getAsBoolean();
            for (Map.Entry<String, SubscribeCallBack> entry : subscribeMap.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().callBack(topic, MessageType.SUBBED, result);
                }
            }
        }
    }

    /**
     * 解除订阅结果
     * 暂时不回调出去，外部只管解除，不管结果
     */
    private void unSubscribeResult(JsonObject msg) {
//        if (this.socketCallBack != null) {
        String topic = msg.get(MessageType.UNSUBBED).getAsString();
        boolean result = msg.get("status").getAsBoolean();
        subTopics.remove(topic);
        unSubTopics.remove(topic);
//            this.socketCallBack.callBack(topic, MessageType.UNSUBBED, result);
//        }
    }
}
