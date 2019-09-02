package com.fmtch.module_bourse.event;

import com.fmtch.module_bourse.bean.MarketBean;

import java.util.List;

/**
 * Created by wtc on 2019/5/20
 */
public class MarketEvent {
    private List<MarketBean> list;
    private boolean isSuccess;

    public List<MarketBean> getList() {
        return list;
    }

    public void setList(List<MarketBean> list) {
        this.list = list;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
