package com.fmtch.module_bourse.bean;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wtc on 2019/5/22
 */
public class DeepBean {

    private List<List<BigDecimal>> asks;
    private List<List<BigDecimal>> bids;

    public List<List<BigDecimal>> getAsks() {
        return asks;
    }

    public void setAsks(List<List<BigDecimal>> asks) {
        this.asks = asks;
    }

    public List<List<BigDecimal>> getBids() {
        return bids;
    }

    public void setBids(List<List<BigDecimal>> bids) {
        this.bids = bids;
    }
}
