package com.fmtch.module_bourse.websocket;

/**
 * Created by wtc on 2019/8/8
 */
public interface TopicType {
    String DEPTH = "depth:%1$s:%2$s";
    String TRADE = "trade:";
    String TICKER = "ticker";
    String TICKER_ASSIGN = "ticker:";
    String KLINE = "kline:%1$s:%2$s";
}
