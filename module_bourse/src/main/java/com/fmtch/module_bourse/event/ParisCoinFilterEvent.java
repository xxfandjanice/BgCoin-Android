package com.fmtch.module_bourse.event;

import java.util.List;

/**
 * Created by wtc on 2019/6/25
 */
public class ParisCoinFilterEvent {
    private String minMoney;
    private List<String> selectedPayMethods;

    public String getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(String minMoney) {
        this.minMoney = minMoney;
    }

    public List<String> getSelectedPayMethods() {
        return selectedPayMethods;
    }

    public void setSelectedPayMethods(List<String> selectedPayMethods) {
        this.selectedPayMethods = selectedPayMethods;
    }
}
