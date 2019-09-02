package com.fmtch.module_bourse.websocket;

/**
 * Created by wtc on 2019/8/8
 */
public interface MessageType {
    //心跳消息
    String PING = "ping";
    //订阅消息
    String SUBBED = "subbed";
    //取消订阅消息
    String UNSUBBED = "unsubbed";
    //主题消息
    String CHANNEL = "channel";
}
