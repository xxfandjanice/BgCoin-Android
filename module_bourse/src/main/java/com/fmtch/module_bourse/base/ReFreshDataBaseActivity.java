package com.fmtch.module_bourse.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.fmtch.base.ui.BaseActivity;
import com.fmtch.base.utils.KeyConstant;
import com.fmtch.base.utils.SpUtils;
import com.fmtch.module_bourse.listener.WebSocketConnectStateListener;
import com.fmtch.module_bourse.utils.NetworkUtils;
import com.fmtch.module_bourse.websocket.OnMessageCallBack;
import com.fmtch.module_bourse.websocket.SubscribeCallBack;
import com.fmtch.module_bourse.websocket.WebSocketManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by wtc on 2019/5/29
 */
public abstract class ReFreshDataBaseActivity extends BaseActivity {

    private IntentFilter filter;
    private int refreshTimes;   //自动刷新频率
    private Disposable disposable;
    private WifiChangedReceiver wifiChangedReceiver;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initWifiChangeReceiver();
    }

    private void initWifiChangeReceiver() {
        wifiChangedReceiver = new WifiChangedReceiver();
        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
    }

    @Override
    public void onResume() {
        super.onResume();
        //刷新行情刷新频率设置
        refreshTimes = (int) SpUtils.get(KeyConstant.KEY_MARKET_REFRESH_TIMES, 3);
        //注册wifi变化的广播
        registerReceiver(wifiChangedReceiver, filter);
        dispose();
        if (refreshTimes != 3) {
            disposable = refreshData(refreshTimes);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //反注册wifi变化的广播
        unregisterReceiver(wifiChangedReceiver);
        //停止实时刷新行情
        dispose();

    }

    /**
     * 监听wifi状态的变化
     */
    private class WifiChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                if (refreshTimes != 3) {
                    return;
                }
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        dispose();
                        //仅在wifi下实时刷新模式下(1s一次),关闭wifi变成普通模式(5s一次)
                        disposable = refreshData(1);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        //仅在wifi下实时刷新数据
                        dispose();
                        disposable = refreshData(3);
                        break;
                }
            }
        }
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 子类实现刷新数据
     */
    public Disposable refreshData(int refreshTimes) {
        //普通 1 (5s一次)
        //始终实时行情 2 (1s一次)
        //仅在wifi下实时刷新 3 (1s一次)
        int period = 5;
        if (refreshTimes == 2 || (refreshTimes == 3 && NetworkUtils.getWifiEnabled())) {
            period = 1;
        }
        return Observable.interval(period, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        pollingData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 轮询数据
     */
    public abstract void pollingData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dispose();
        //解除订阅
        WebSocketManager.getInstance().unSubscribes(getSubscribeKey(), getTopics());
    }

    /**
     * 订阅的主题
     *
     * @return
     */
    public abstract List<String> getTopics();

    /**
     * 订阅主题回调
     *
     * @return
     */
    public abstract SubscribeCallBack getSubscribeCallBack();

    /**
     * 订阅主题的消息回调
     *
     * @return
     */
    public abstract OnMessageCallBack getOnMessageCallBack();

    /**
     * Socket是否连接成功的回调
     *
     * @return
     */
    public abstract WebSocketConnectStateListener getSocketConnectListener();

    /**
     * 订阅的Key,用于页面解除订阅
     *
     * @return
     */
    public abstract String getSubscribeKey();

    /**
     * 手动发起订阅
     */
    public void subscribes() {
        WebSocketManager.getInstance().subscribes(getSubscribeKey(), getTopics(), getSubscribeCallBack(), getOnMessageCallBack(),getSocketConnectListener());
    }

    /**
     * 手动发起订阅
     */
    public void subscribe(String topic) {
        WebSocketManager.getInstance().subscribe(getSubscribeKey(), topic, getSubscribeCallBack(), getOnMessageCallBack(),getSocketConnectListener());
    }


    /**
     * 手动解除订阅
     */
    public void unSubscribes() {
        WebSocketManager.getInstance().unSubscribes(getSubscribeKey(), getTopics());
    }

    /**
     * 手动解除订阅
     */
    public void unSubscribe(String topic) {
        WebSocketManager.getInstance().unSubscribe(getSubscribeKey(), topic);
    }
}
