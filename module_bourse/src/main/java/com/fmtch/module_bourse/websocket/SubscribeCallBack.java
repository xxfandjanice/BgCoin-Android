package com.fmtch.module_bourse.websocket;

/**
 * Created by wtc on 2019/8/8
 */
public interface SubscribeCallBack {
    void callBack(String topic, String action, boolean result);
}
