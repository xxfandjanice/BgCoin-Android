package com.fmtch.module_bourse.websocket;

import java.io.Serializable;

/**
 * Created by wtc on 2019/8/8
 */
public class BaseMessage<T> implements Serializable {
    private String channel;
    private T data;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
