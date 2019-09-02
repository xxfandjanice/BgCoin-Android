package com.fmtch.module_bourse.bean.response;

import com.fmtch.module_bourse.bean.OrderBookBean;
import com.fmtch.module_bourse.bean.ParisBuySellBean;

import java.io.Serializable;

public class ParisBuySellResponse implements Serializable {

    private OrderBookBean order_book;

    private ParisBuySellBean sell_info;

    private ParisBuySellBean buy_info;

    public OrderBookBean getOrder_book() {
        return order_book;
    }

    public void setOrder_book(OrderBookBean order_book) {
        this.order_book = order_book;
    }

    public ParisBuySellBean getSell_info() {
        return sell_info;
    }

    public void setSell_info(ParisBuySellBean sell_info) {
        this.sell_info = sell_info;
    }

    public ParisBuySellBean getBuy_info() {
        return buy_info;
    }

    public void setBuy_info(ParisBuySellBean buy_info) {
        this.buy_info = buy_info;
    }
}
